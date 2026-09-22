package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.common.enums.AnniversaryTypeEnum;
import cn.codesensi.amour.common.enums.BaseEnum;
import cn.codesensi.amour.common.enums.HiddenEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.support.AuditUserFiller;
import cn.codesensi.amour.mapper.PortalAnniversaryMapper;
import cn.codesensi.amour.model.converter.AnniversaryConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.PortalAnniversary;
import cn.codesensi.amour.service.AnniversaryService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.RawQueryColumn;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.codesensi.amour.model.entity.table.PortalAnniversaryTableDef.PORTAL_ANNIVERSARY;

/**
 * 门户纪念日 Service 实现 —— 门户下发与管理端维护共用。
 * <p>
 * 实体仅在层内流转：查询结果经转换器映射为 {@code AnniversaryDTO} 后返回，
 * 新增/修改接收 DTO 并在内部完成实体组装。
 * <p>
 * 门户排序为「下一次发生日」升序，以数据库排序键表达式实现（见
 * {@link #nextOccurrenceColumn()}），分页由数据库完成，与足迹门户分页同构。
 *
 * @author codesensi
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AnniversaryServiceImpl implements AnniversaryService {

    private final PortalAnniversaryMapper portalAnniversaryMapper;
    private final AnniversaryConverter anniversaryConverter;
    private final AuditUserFiller auditUserFiller;

    /**
     * 门户纪念日分页（免登录）。
     * <p>
     * 显隐口径固定为「仅显示」（hidden 强制过滤为 0，管理端维护口径之外的安全边界）；
     * 排序为下一次发生日升序（排序键见 {@link #nextOccurrenceColumn()}）→ id 升序。
     *
     * @param page 分页参数
     * @return 纪念日条目 DTO 分页结果
     */
    @Override
    public Page<AnniversaryDTO> pagePortal(BasePage page) {
        return doPage(page, null, HiddenEnum.SHOW.getCode());
    }

    /**
     * 下一次发生的纪念日（免登录，首页卡片专用）：同门户排序取首条，无数据返回 null。
     *
     * @return 最近一条纪念日条目 DTO；无数据时返回 null
     */
    @Override
    public AnniversaryDTO next() {
        PortalAnniversary entity = QueryChain.of(portalAnniversaryMapper)
                .where(PORTAL_ANNIVERSARY.HIDDEN.eq(HiddenEnum.SHOW.getCode()))
                .orderBy(nextOccurrenceColumn(), true)
                .orderBy(PORTAL_ANNIVERSARY.ID, true)
                .one();
        return entity == null ? null : anniversaryConverter.toDTO(entity);
    }

    /**
     * 管理端纪念日分页（全量）。
     * <p>
     * 名称模糊匹配，显隐为精确匹配（条件缺省时自动忽略），
     * 排序为下一次发生日升序 → id 升序（与门户口径一致）。
     *
     * @param pageDTO 分页查询参数 DTO
     * @return 纪念日条目 DTO 分页结果
     */
    @Override
    public Page<AnniversaryDTO> pageAdmin(AnniversaryPageDTO pageDTO) {
        Page<AnniversaryDTO> result = doPage(pageDTO, pageDTO.getName(), pageDTO.getHidden());
        auditUserFiller.fill(result.getRecords());
        return result;
    }

    /**
     * 分页查询内核 —— 门户与管理端分页共用的条件装配与分页执行：
     * 名称模糊匹配，显隐精确匹配（条件缺省时自动忽略）；
     * 排序为下一次发生日升序（排序键见 {@link #nextOccurrenceColumn()}）→ id 升序。
     *
     * @param page   分页参数
     * @param name   纪念日名称（模糊匹配，可空）
     * @param hidden 显隐过滤（可空;门户固定传显示，管理端传筛选值或 null 查全量）
     * @return 纪念日条目 DTO 分页结果
     */
    private Page<AnniversaryDTO> doPage(BasePage page, String name, Integer hidden) {
        Page<PortalAnniversary> entityPage = QueryChain.of(portalAnniversaryMapper)
                .where(PORTAL_ANNIVERSARY.NAME.like(name, StrUtil::isNotBlank))
                .and(PORTAL_ANNIVERSARY.HIDDEN.eq(hidden, ObjUtil::isNotNull))
                .orderBy(nextOccurrenceColumn(), true)
                .orderBy(PORTAL_ANNIVERSARY.ID, true)
                .page(Page.of(page.getPageNumber(), page.getPageSize()));
        return anniversaryConverter.toPageDTO(entityPage);
    }

    /**
     * 门户排序键 —— 下一次发生日（与前端 utils/anniversary 的 nextOccurrenceDays 口径一致）：
     * 每年重复取今年/明年的同月日（当天算今年）；一次性日期仅在未来时为其自身，已过去垫底。
     * <p>
     * CASE 各分支统一输出 yyyy-MM-dd 字符串，ISO 字符串序即时间序，
     * 并天然规避 2-29 闰日在平年的落位问题。
     *
     * @return 下一次发生日排序键（原生 SQL 表达式）
     */
    private RawQueryColumn nextOccurrenceColumn() {
        return new RawQueryColumn("""
                CASE
                    WHEN `repeat_yearly` = 1 THEN
                        CASE WHEN RIGHT(`anniversary_date`, 5) >= RIGHT(CAST(CURRENT_DATE AS VARCHAR), 5)
                             THEN CONCAT(YEAR(CURRENT_DATE), RIGHT(`anniversary_date`, 5))
                             ELSE CONCAT(YEAR(CURRENT_DATE) + 1, RIGHT(`anniversary_date`, 5)) END
                    WHEN `anniversary_date` >= CURRENT_DATE THEN CAST(`anniversary_date` AS VARCHAR)
                    ELSE '9999-12-31'
                END""");
    }

    /**
     * 修改纪念日显隐（对齐足迹既有单列状态更新惯例：存在性校验 + 同状态幂等返回，
     * 仅覆盖 hidden 字段）。
     *
     * @param changeHiddenDTO 显隐状态信息
     */
    @Override
    public void changeHidden(AnniversaryChangeHiddenDTO changeHiddenDTO) {
        PortalAnniversary entity = QueryChain.of(portalAnniversaryMapper)
                .select(PORTAL_ANNIVERSARY.ID, PORTAL_ANNIVERSARY.HIDDEN)
                .where(PORTAL_ANNIVERSARY.ID.eq(changeHiddenDTO.getId()))
                .one();
        if (ObjUtil.isNull(entity)) {
            throw new BusinessException("纪念日不存在");
        }

        // 显隐一致时幂等返回
        if (changeHiddenDTO.getHidden().equals(entity.getHidden())) {
            return;
        }

        PortalAnniversary item = new PortalAnniversary();
        item.setId(changeHiddenDTO.getId());
        item.setHidden(changeHiddenDTO.getHidden());
        portalAnniversaryMapper.update(item);
    }

    /**
     * {@inheritDoc}
     * <p>
     * type 编码经 {@link AnniversaryTypeEnum} 解析校验（字典展示与枚举校验的既有分工），
     * 非法值整单失败。
     */
    @Override
    public void insert(AnniversaryInsertDTO insertDTO) {
        assertTypeInDict(insertDTO.getType());
        PortalAnniversary entity = anniversaryConverter.toEntity(insertDTO);
        portalAnniversaryMapper.insert(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(AnniversaryUpdateDTO updateDTO) {
        PortalAnniversary entity = QueryChain.of(portalAnniversaryMapper)
                .where(PORTAL_ANNIVERSARY.ID.eq(updateDTO.getId()))
                .one();
        if (ObjUtil.isNull(entity)) {
            throw new BusinessException("纪念日不存在");
        }
        assertTypeInDict(updateDTO.getType());
        portalAnniversaryMapper.update(anniversaryConverter.toEntity(updateDTO));
    }

    /**
     * 校验类型编码为字典 anniversary-type 的合法取值（AnniversaryTypeEnum 反查）。
     *
     * @param type 类型编码
     */
    private void assertTypeInDict(String type) {
        if (BaseEnum.fromCode(AnniversaryTypeEnum.class, type) == null) {
            throw new BusinessException("纪念日类型不合法");
        }
    }

    /**
     * 批量逻辑删除纪念日（对齐足迹既有 delete 惯例：任一 id 不存在时整批失败）。
     *
     * @param ids 纪念日ID集合
     */
    @Override
    public void delete(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<Long> distinctIds = ids.stream().distinct().toList();
        List<Long> existingIds = QueryChain.of(portalAnniversaryMapper)
                .select(PORTAL_ANNIVERSARY.ID)
                .where(PORTAL_ANNIVERSARY.ID.in(distinctIds))
                .listAs(Long.class);
        if (existingIds.size() < distinctIds.size()) {
            throw new BusinessException("纪念日不存在");
        }
        portalAnniversaryMapper.deleteBatchByIds(distinctIds);
    }

}
