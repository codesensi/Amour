package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.common.enums.HiddenEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.mapper.PortalFootprintMapper;
import cn.codesensi.amour.model.converter.FootprintConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.PortalFootprint;
import cn.codesensi.amour.service.FootprintService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static cn.codesensi.amour.model.entity.table.PortalFootprintTableDef.PORTAL_FOOTPRINT;

/**
 * 足迹地图 Service 实现 —— 门户下发与管理端维护共用。
 * <p>
 * 实体仅在层内流转：查询结果经转换器映射为 {@code FootprintItemDTO} 后返回，
 * 新增/修改接收 DTO 并在内部完成实体组装。
 *
 * @author codesensi
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class FootprintServiceImpl implements FootprintService {

    private final PortalFootprintMapper portalFootprintMapper;
    private final FootprintConverter footprintConverter;

    /**
     * 门户足迹分页（免登录）。
     * <p>
     * 显隐口径固定为「仅显示」（hidden 强制过滤为 0，管理端维护口径之外的安全边界），
     * 排序为到访日期升序 → id 升序。
     *
     * @param page 分页参数
     * @return 足迹条目 DTO 分页结果
     */
    @Override
    public Page<FootprintDTO> pagePortal(BasePage page) {
        return doPage(page, null, null, null, HiddenEnum.SHOW.getCode());
    }

    /**
     * 门户足迹地图全量点集（免登录）。
     * <p>
     * 显隐口径与门户分页一致（仅显示），排序为到访日期升序 → id 升序
     * （与 {@link #pagePortal} 一致，首页地图连线依旅程推进）；
     * {@code LIMIT 1000} 防御性截断，无分页 COUNT 开销。
     *
     * @return 地图点集 DTO 列表
     */
    @Override
    public List<FootprintDTO> listMapPoints() {
        List<PortalFootprint> entities = QueryChain.of(portalFootprintMapper)
                .where(PORTAL_FOOTPRINT.HIDDEN.eq(HiddenEnum.SHOW.getCode()))
                .orderBy(PORTAL_FOOTPRINT.ARRIVAL_DATE, true)
                .orderBy(PORTAL_FOOTPRINT.ID, true)
                .list();
        return entities.stream()
                .map(footprintConverter::toDTO)
                .toList();
    }

    /**
     * 管理端足迹分页（全量）。
     * <p>
     * 城市为模糊匹配，到访日期为闭区间范围过滤，显隐为精确匹配，条件缺省时自动忽略。
     *
     * @param pageDTO 分页查询参数 DTO
     * @return 足迹条目 DTO 分页结果
     */
    @Override
    public Page<FootprintDTO> pageAdmin(FootprintPageDTO pageDTO) {
        return doPage(pageDTO, pageDTO.getCity(), pageDTO.getArrivalDateBegin(), pageDTO.getArrivalDateEnd(), pageDTO.getHidden());
    }

    /**
     * 分页查询内核 —— 门户与管理端分页共用的条件装配与分页执行。
     * <p>
     * 城市为模糊匹配，到访日期为闭区间范围过滤（经纬度未建立空间索引，
     * 私人足迹数据量级小，仅依赖 arrival_date 索引按日期排序）；
     * 排序为到访日期升序 → id 升序（无日期的记录按 NULL 值排序规则置于最前/最后由数据库决定）。
     *
     * @param page             分页参数
     * @param city             城市（模糊匹配，可空）
     * @param arrivalDateBegin 到访日期起点（含，可空）
     * @param arrivalDateEnd   到访日期终点（含，可空）
     * @param hidden           显隐过滤（可空;门户固定传 0，管理端传筛选值或 null 查全量）
     * @return 足迹条目 DTO 分页结果
     */
    private Page<FootprintDTO> doPage(BasePage page, String city, LocalDate arrivalDateBegin, LocalDate arrivalDateEnd, Integer hidden) {
        Page<PortalFootprint> entityPage = QueryChain.of(portalFootprintMapper)
                .where(PORTAL_FOOTPRINT.CITY.like(city, StrUtil::isNotBlank))
                .and(PORTAL_FOOTPRINT.ARRIVAL_DATE.ge(arrivalDateBegin, ObjUtil::isNotNull))
                .and(PORTAL_FOOTPRINT.ARRIVAL_DATE.le(arrivalDateEnd, ObjUtil::isNotNull))
                .and(PORTAL_FOOTPRINT.HIDDEN.eq(hidden, ObjUtil::isNotNull))
                .orderBy(PORTAL_FOOTPRINT.ARRIVAL_DATE, true)
                .orderBy(PORTAL_FOOTPRINT.ID, true)
                .page(Page.of(page.getPageNumber(), page.getPageSize()));
        return footprintConverter.toPageDTO(entityPage);
    }

    /**
     * 新增足迹：主键由全局雪花配置生成，照片以 URL 直存（photoUrl，无跨表校验，
     * 对齐画册 url 直存的宽松口径，前端上传组件已保证引用有效）。
     *
     * @param insertDTO 新增参数
     */
    @Override
    public void insert(FootprintInsertDTO insertDTO) {
        PortalFootprint entity = footprintConverter.toEntity(insertDTO);
        portalFootprintMapper.insert(entity);
    }

    /**
     * 修改足迹：存在性校验后经 UpdateChain 显式逐列赋值更新（对齐画册既有 update 惯例，
     * 足迹不存在时抛出业务异常）。显式赋值使 null 均真实写入（如清空照片/经纬度），
     * 不受忽略 null 策略影响。
     *
     * @param updateDTO 修改参数（id 必填）
     */
    @Override
    public void update(FootprintUpdateDTO updateDTO) {
        PortalFootprint footprint = QueryChain.of(portalFootprintMapper)
                .select(PORTAL_FOOTPRINT.ID)
                .where(PORTAL_FOOTPRINT.ID.eq(updateDTO.getId()))
                .one();
        if (ObjUtil.isNull(footprint)) {
            throw new BusinessException("足迹不存在");
        }
        UpdateChain.of(PortalFootprint.class)
                .set(PORTAL_FOOTPRINT.CITY, updateDTO.getCity())
                .set(PORTAL_FOOTPRINT.PLACE_NAME, updateDTO.getPlaceName())
                .set(PORTAL_FOOTPRINT.LONGITUDE, updateDTO.getLongitude())
                .set(PORTAL_FOOTPRINT.LATITUDE, updateDTO.getLatitude())
                .set(PORTAL_FOOTPRINT.ARRIVAL_DATE, updateDTO.getArrivalDate())
                .set(PORTAL_FOOTPRINT.PHOTO_URL, updateDTO.getPhotoUrl())
                .set(PORTAL_FOOTPRINT.REMARK, updateDTO.getRemark())
                .where(PORTAL_FOOTPRINT.ID.eq(updateDTO.getId()))
                .update();
    }

    /**
     * 修改足迹显隐（对齐画册既有单列状态更新惯例：存在性校验 + 同状态幂等返回，
     * 仅覆盖 hidden 字段）。
     *
     * @param changeHiddenDTO 显隐状态信息
     */
    @Override
    public void changeHidden(FootprintChangeHiddenDTO changeHiddenDTO) {
        PortalFootprint footprint = QueryChain.of(portalFootprintMapper)
                .select(PORTAL_FOOTPRINT.ID, PORTAL_FOOTPRINT.HIDDEN)
                .where(PORTAL_FOOTPRINT.ID.eq(changeHiddenDTO.getId()))
                .one();
        if (ObjUtil.isNull(footprint)) {
            throw new BusinessException("足迹不存在");
        }

        // 显隐一致时幂等返回
        if (changeHiddenDTO.getHidden().equals(footprint.getHidden())) {
            return;
        }

        PortalFootprint entity = new PortalFootprint();
        entity.setId(changeHiddenDTO.getId());
        entity.setHidden(changeHiddenDTO.getHidden());
        portalFootprintMapper.update(entity);
    }

    /**
     * 批量逻辑删除足迹（对齐画册既有 delete 惯例：任一 id 不存在时整批失败）。
     *
     * @param ids 足迹ID集合
     */
    @Override
    public void delete(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<Long> distinctIds = ids.stream().distinct().toList();
        List<Long> existingIds = QueryChain.of(portalFootprintMapper)
                .select(PORTAL_FOOTPRINT.ID)
                .where(PORTAL_FOOTPRINT.ID.in(distinctIds))
                .listAs(Long.class);
        if (existingIds.size() < distinctIds.size()) {
            throw new BusinessException("足迹不存在");
        }
        portalFootprintMapper.deleteBatchByIds(distinctIds);
    }

}
