package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.mapper.PortalFootprintMapper;
import cn.codesensi.amour.model.converter.FootprintConverter;
import cn.codesensi.amour.model.dto.FootprintDTO;
import cn.codesensi.amour.model.dto.FootprintInsertDTO;
import cn.codesensi.amour.model.dto.FootprintPageDTO;
import cn.codesensi.amour.model.dto.FootprintUpdateDTO;
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
     * 条件固定为空（逻辑删除由全局配置过滤），排序为到访日期升序 → id 升序。
     *
     * @param page 分页参数
     * @return 足迹条目 DTO 分页结果
     */
    @Override
    public Page<FootprintDTO> pagePortal(BasePage page) {
        return doPage(page, null, null, null);
    }

    /**
     * 管理端足迹分页（全量）。
     * <p>
     * 城市为模糊匹配，到访日期为闭区间范围过滤，条件缺省时自动忽略。
     *
     * @param pageDTO 分页查询参数 DTO
     * @return 足迹条目 DTO 分页结果
     */
    @Override
    public Page<FootprintDTO> pageAdmin(FootprintPageDTO pageDTO) {
        return doPage(pageDTO, pageDTO.getCity(), pageDTO.getArrivalDateBegin(), pageDTO.getArrivalDateEnd());
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
     * @return 足迹条目 DTO 分页结果
     */
    private Page<FootprintDTO> doPage(BasePage page, String city, LocalDate arrivalDateBegin, LocalDate arrivalDateEnd) {
        Page<PortalFootprint> entityPage = QueryChain.of(portalFootprintMapper)
                .where(PORTAL_FOOTPRINT.CITY.like(city, StrUtil::isNotBlank))
                .and(PORTAL_FOOTPRINT.ARRIVAL_DATE.ge(arrivalDateBegin, ObjUtil::isNotNull))
                .and(PORTAL_FOOTPRINT.ARRIVAL_DATE.le(arrivalDateEnd, ObjUtil::isNotNull))
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
