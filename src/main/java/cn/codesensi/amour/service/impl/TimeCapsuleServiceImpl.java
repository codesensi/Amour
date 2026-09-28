package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.common.enums.DataModuleEnum;
import cn.codesensi.amour.common.enums.HiddenEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.support.AuditUserFiller;
import cn.codesensi.amour.mapper.PortalTimeCapsuleMapper;
import cn.codesensi.amour.model.converter.TimeCapsuleConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.PortalTimeCapsule;
import cn.codesensi.amour.service.DataScopeService;
import cn.codesensi.amour.service.TimeCapsuleService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static cn.codesensi.amour.model.entity.table.PortalTimeCapsuleTableDef.PORTAL_TIME_CAPSULE;

/**
 * 时间胶囊 Service 实现 —— 门户下发与管理端维护共用。
 * <p>
 * 封存安全由本层保证：门户口径下未到 {@code open_time} 的记录将 content 置空后再下发，
 * 解锁标识 {@code unlocked} 也由本层按当前时间判定，前端不参与解锁判定；
 * 管理端口径按数据范围策略（{@link DataScopeService}）行过滤与回填 canEdit，
 * 未到期的信件仅创建人（或可见范围为 all 的维护者）可见内容，其余遮罩。
 *
 * @author codesensi
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class TimeCapsuleServiceImpl implements TimeCapsuleService {

    private final PortalTimeCapsuleMapper portalTimeCapsuleMapper;
    private final TimeCapsuleConverter timeCapsuleConverter;
    private final AuditUserFiller auditUserFiller;
    private final DataScopeService dataScopeService;

    /**
     * 门户时间胶囊分页（免登录）。
     * <p>
     * 显隐口径固定为「仅显示」（hidden 强制过滤为 0，管理端维护口径之外的安全边界）；
     * 排序为解锁时间升序 → id 升序（即将解锁的排最前）；未解锁记录遮罩内容。
     *
     * @param page 分页参数
     * @return 时间胶囊 DTO 分页结果
     */
    @Override
    public Page<TimeCapsuleDTO> pagePortal(BasePage page) {
        return doPage(page, null, HiddenEnum.SHOW.getCode(), true, null);
    }

    /**
     * 管理端时间胶囊分页（全量，含隐藏项与未解锁项）。
     * <p>
     * 标题为模糊匹配，显隐为精确匹配，条件缺省时自动忽略；
     * 行过滤按数据范围策略（可见范围 self 时仅本人创建的记录）；
     * 未到期的信件仅创建人（或可见范围为 all 的维护者）可见内容，其余置空防剧透；
     * 回填 canEdit 与创建人/更新人用户名。
     *
     * @param pageDTO 分页查询参数 DTO
     * @return 时间胶囊 DTO 分页结果
     */
    @Override
    public Page<TimeCapsuleDTO> pageAdmin(TimeCapsulePageDTO pageDTO) {
        Page<TimeCapsuleDTO> result = doPage(pageDTO, pageDTO.getTitle(), pageDTO.getHidden(), false,
                dataScopeService.visibleOwnerFilter(DataModuleEnum.TIME_CAPSULE));
        result.getRecords().forEach(this::markAdminRowState);
        auditUserFiller.fill(result.getRecords());
        return result;
    }

    /**
     * 新增时间胶囊：标题/内容/解锁时间/显隐由表单传入，主键由全局雪花配置生成。
     *
     * @param insertDTO 新增参数
     */
    @Override
    public void insert(TimeCapsuleInsertDTO insertDTO) {
        PortalTimeCapsule entity = timeCapsuleConverter.toEntity(insertDTO);
        portalTimeCapsuleMapper.insert(entity);
    }

    /**
     * 修改时间胶囊：存在性校验后经 UpdateChain 显式逐列赋值更新（对齐清单等既有
     * update 惯例，胶囊不存在时抛出业务异常）。显式赋值使 null/空串均真实写入，
     * 不受忽略 null 策略影响；显隐不经本方法维护（单独走 {@link #changeHidden}）。
     *
     * @param updateDTO 修改参数（id 必填）
     */
    @Override
    public void update(TimeCapsuleUpdateDTO updateDTO) {
        PortalTimeCapsule capsule = QueryChain.of(portalTimeCapsuleMapper)
                .select(PORTAL_TIME_CAPSULE.ID, PORTAL_TIME_CAPSULE.CREATOR)
                .where(PORTAL_TIME_CAPSULE.ID.eq(updateDTO.getId()))
                .one();
        if (ObjUtil.isNull(capsule)) {
            throw new BusinessException("胶囊不存在");
        }
        dataScopeService.assertEditable(DataModuleEnum.TIME_CAPSULE, capsule.getCreator());
        UpdateChain.of(PortalTimeCapsule.class)
                .set(PORTAL_TIME_CAPSULE.TITLE, updateDTO.getTitle())
                .set(PORTAL_TIME_CAPSULE.CONTENT, updateDTO.getContent())
                .set(PORTAL_TIME_CAPSULE.OPEN_TIME, updateDTO.getOpenTime())
                .where(PORTAL_TIME_CAPSULE.ID.eq(updateDTO.getId()))
                .update();
    }

    /**
     * 修改时间胶囊显隐（对齐清单等既有单列状态更新惯例：存在性校验 + 同状态幂等返回，
     * 仅覆盖 hidden 字段）。
     *
     * @param changeHiddenDTO 显隐状态信息
     */
    @Override
    public void changeHidden(TimeCapsuleChangeHiddenDTO changeHiddenDTO) {
        PortalTimeCapsule capsule = QueryChain.of(portalTimeCapsuleMapper)
                .select(PORTAL_TIME_CAPSULE.ID, PORTAL_TIME_CAPSULE.CREATOR, PORTAL_TIME_CAPSULE.HIDDEN)
                .where(PORTAL_TIME_CAPSULE.ID.eq(changeHiddenDTO.getId()))
                .one();
        if (ObjUtil.isNull(capsule)) {
            throw new BusinessException("胶囊不存在");
        }
        dataScopeService.assertEditable(DataModuleEnum.TIME_CAPSULE, capsule.getCreator());

        // 显隐一致时幂等返回
        if (changeHiddenDTO.getHidden().equals(capsule.getHidden())) {
            return;
        }

        PortalTimeCapsule entity = new PortalTimeCapsule();
        entity.setId(changeHiddenDTO.getId());
        entity.setHidden(changeHiddenDTO.getHidden());
        portalTimeCapsuleMapper.update(entity);
    }

    /**
     * 批量逻辑删除时间胶囊（对齐清单等既有 delete 惯例：任一 id 不存在时整批失败；
     * 数据范围按行校验归属，任一越权整批失败）。
     *
     * @param ids 胶囊ID集合
     */
    @Override
    public void delete(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<Long> distinctIds = ids.stream().distinct().toList();
        List<PortalTimeCapsule> existing = QueryChain.of(portalTimeCapsuleMapper)
                .select(PORTAL_TIME_CAPSULE.ID, PORTAL_TIME_CAPSULE.CREATOR)
                .where(PORTAL_TIME_CAPSULE.ID.in(distinctIds))
                .list();
        if (existing.size() < distinctIds.size()) {
            throw new BusinessException("胶囊不存在");
        }
        existing.forEach(capsule ->
                dataScopeService.assertEditable(DataModuleEnum.TIME_CAPSULE, capsule.getCreator()));
        portalTimeCapsuleMapper.deleteBatchByIds(distinctIds);
    }

    /**
     * 分页查询内核 —— 门户与管理端分页共用的条件装配与分页执行。
     * <p>
     * 标题为模糊匹配，显隐与行归属为精确匹配，条件缺省时自动忽略；
     * 排序为解锁时间升序 → id 升序；逻辑删除（del_flag）由全局配置自动追加过滤。
     *
     * @param page         分页参数
     * @param title        标题（模糊匹配，可空）
     * @param hidden       显隐过滤（可空;门户固定传 0，管理端传筛选值或 null 查全量）
     * @param maskUnopened 是否对未解锁记录遮罩内容并判定解锁标识（门户 true，管理端 false）
     * @param ownerFilter  行归属过滤值（数据范围策略；null 不过滤，管理端 self 时为当前用户ID）
     * @return 时间胶囊 DTO 分页结果
     */
    private Page<TimeCapsuleDTO> doPage(BasePage page, String title, Integer hidden, boolean maskUnopened, Long ownerFilter) {
        Page<PortalTimeCapsule> entityPage = QueryChain.of(portalTimeCapsuleMapper)
                .where(PORTAL_TIME_CAPSULE.TITLE.like(title, StrUtil::isNotBlank))
                .and(PORTAL_TIME_CAPSULE.HIDDEN.eq(hidden, ObjUtil::isNotNull))
                .and(PORTAL_TIME_CAPSULE.CREATOR.eq(ownerFilter, ObjUtil::isNotNull))
                .orderBy(PORTAL_TIME_CAPSULE.OPEN_TIME, true)
                .orderBy(PORTAL_TIME_CAPSULE.ID, true)
                .page(Page.of(page.getPageNumber(), page.getPageSize()));
        Page<TimeCapsuleDTO> result = timeCapsuleConverter.toPageDTO(entityPage);
        if (maskUnopened) {
            // 封存安全:门户口径下未到解锁时间的记录不下发内容，解锁标识按当前时间判定
            result.getRecords().forEach(this::markUnlockState);
        }
        return result;
    }

    /**
     * 管理端行状态回填：按当前时间判定解锁标识、按数据范围策略回填 canEdit；
     * 封存遮罩特例——未到期的信件仅创建人（或可见范围为 all 的维护者）可见内容，
     * 其余记录 content 置空，避免非创建人经管理端提前读到信件内容。
     *
     * @param itemDTO 时间胶囊条目 DTO
     */
    private void markAdminRowState(TimeCapsuleDTO itemDTO) {
        boolean unlocked = ObjUtil.isNotNull(itemDTO.getOpenTime())
                && LocalDateTime.now().isAfter(itemDTO.getOpenTime());
        itemDTO.setUnlocked(unlocked);
        itemDTO.setCanEdit(dataScopeService.canEdit(DataModuleEnum.TIME_CAPSULE, itemDTO.getCreator()));
        if (!unlocked && !dataScopeService.canSee(DataModuleEnum.TIME_CAPSULE, itemDTO.getCreator())) {
            itemDTO.setContent(null);
        }
    }

    /**
     * 按当前时间判定解锁状态：未解锁记录将 content 置空（防抓包剧透），
     * unlocked 标识写入 DTO（门户响应经同名映射下发）。
     *
     * @param itemDTO 时间胶囊条目 DTO
     */
    private void markUnlockState(TimeCapsuleDTO itemDTO) {
        boolean unlocked = ObjUtil.isNotNull(itemDTO.getOpenTime())
                && LocalDateTime.now().isAfter(itemDTO.getOpenTime());
        itemDTO.setUnlocked(unlocked);
        if (!unlocked) {
            itemDTO.setContent(null);
        }
    }

}
