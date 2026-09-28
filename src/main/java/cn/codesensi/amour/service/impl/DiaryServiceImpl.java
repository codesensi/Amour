package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.common.enums.BaseEnum;
import cn.codesensi.amour.common.enums.DataModuleEnum;
import cn.codesensi.amour.common.enums.DiaryMoodEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.support.AuditUserFiller;
import cn.codesensi.amour.common.support.AuthorInfoFiller;
import cn.codesensi.amour.mapper.PortalDiaryMapper;
import cn.codesensi.amour.model.converter.DiaryConverter;
import cn.codesensi.amour.model.dto.DiaryDTO;
import cn.codesensi.amour.model.dto.DiaryInsertDTO;
import cn.codesensi.amour.model.dto.DiaryPageDTO;
import cn.codesensi.amour.model.dto.DiaryUpdateDTO;
import cn.codesensi.amour.model.entity.PortalDiary;
import cn.codesensi.amour.service.DataScopeService;
import cn.codesensi.amour.service.DiaryService;
import cn.dev33.satoken.stp.StpUtil;
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

import static cn.codesensi.amour.model.entity.table.PortalDiaryTableDef.PORTAL_DIARY;

/**
 * 情侣日记 Service 实现 —— 门户下发与管理端维护共用。
 * <p>
 * 记录人归属由本层保证：新增时取当前登录人填充，修改不触碰 user_id；
 * 记录人昵称/头像按本页 userId 批量查用户表回填（单次查询，对齐
 * {@code AuditUserFiller} 的批量回填思想）。
 *
 * @author codesensi
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private final PortalDiaryMapper portalDiaryMapper;
    private final DiaryConverter diaryConverter;
    private final AuditUserFiller auditUserFiller;
    private final AuthorInfoFiller authorInfoFiller;
    private final DataScopeService dataScopeService;

    /**
     * 门户情侣日记分页（免登录）。
     * <p>
     * 排序为记录日期降序 → id 降序（最近的日记在前）；
     * 记录人昵称/头像由本层批量回填。
     *
     * @param page 分页参数
     * @return 情侣日记 DTO 分页结果
     */
    @Override
    public Page<DiaryDTO> pagePortal(BasePage page) {
        Page<DiaryDTO> result = doPage(page, null, null, null, null);
        authorInfoFiller.fill(result.getRecords());
        return result;
    }

    /**
     * 管理端情侣日记分页（全量）。
     * <p>
     * 记录人/记录日期/心情为精确匹配，条件缺省时自动忽略；
     * 行过滤按数据范围策略（可见范围 self 时仅本人记录的日记）；
     * 回填 canEdit 与审计用户名、记录人展示信息。
     *
     * @param pageDTO 分页查询参数 DTO
     * @return 情侣日记 DTO 分页结果
     */
    @Override
    public Page<DiaryDTO> pageAdmin(DiaryPageDTO pageDTO) {
        Page<DiaryDTO> result = doPage(pageDTO, pageDTO.getUserId(), pageDTO.getDiaryDate(), pageDTO.getMood(),
                dataScopeService.visibleOwnerFilter(DataModuleEnum.DIARY));
        result.getRecords().forEach(item ->
                item.setCanEdit(dataScopeService.canEdit(DataModuleEnum.DIARY, item.getUserId())));
        auditUserFiller.fill(result.getRecords());
        authorInfoFiller.fill(result.getRecords());
        return result;
    }

    /**
     * 新增情侣日记：记录日期/心情/内容由表单传入，
     * 记录人取当前登录人（日记归属与登录者绑定，不接收 userId）。
     *
     * @param insertDTO 新增参数
     */
    @Override
    public void insert(DiaryInsertDTO insertDTO) {
        insertDTO.setMood(normalizeMood(insertDTO.getMood()));
        PortalDiary entity = diaryConverter.toEntity(insertDTO);
        entity.setUserId(StpUtil.getLoginIdAsLong());
        portalDiaryMapper.insert(entity);
    }

    /**
     * 修改情侣日记：存在性校验后经 UpdateChain 显式逐列赋值更新（对齐清单等既有
     * update 惯例，日记不存在时抛出业务异常）。记录人归属不可变（user_id 不参与更新）。
     *
     * @param updateDTO 修改参数（id 必填）
     */
    @Override
    public void update(DiaryUpdateDTO updateDTO) {
        PortalDiary diary = QueryChain.of(portalDiaryMapper)
                .select(PORTAL_DIARY.ID, PORTAL_DIARY.USER_ID)
                .where(PORTAL_DIARY.ID.eq(updateDTO.getId()))
                .one();
        if (ObjUtil.isNull(diary)) {
            throw new BusinessException("日记不存在");
        }
        dataScopeService.assertEditable(DataModuleEnum.DIARY, diary.getUserId());
        String mood = normalizeMood(updateDTO.getMood());
        UpdateChain.of(PortalDiary.class)
                .set(PORTAL_DIARY.DIARY_DATE, updateDTO.getDiaryDate())
                .set(PORTAL_DIARY.MOOD, mood)
                .set(PORTAL_DIARY.CONTENT, updateDTO.getContent())
                .where(PORTAL_DIARY.ID.eq(updateDTO.getId()))
                .update();
    }

    /**
     * 批量逻辑删除情侣日记（对齐清单等既有 delete 惯例：任一 id 不存在时整批失败）。
     *
     * @param ids 日记ID集合
     */
    /**
     * 批量逻辑删除情侣日记（对齐清单等既有 delete 惯例：任一 id 不存在时整批失败；
     * 数据范围按行校验归属，任一越权整批失败）。
     *
     * @param ids 日记ID集合
     */
    @Override
    public void delete(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<Long> distinctIds = ids.stream().distinct().toList();
        List<PortalDiary> existing = QueryChain.of(portalDiaryMapper)
                .select(PORTAL_DIARY.ID, PORTAL_DIARY.USER_ID)
                .where(PORTAL_DIARY.ID.in(distinctIds))
                .list();
        if (existing.size() < distinctIds.size()) {
            throw new BusinessException("日记不存在");
        }
        existing.forEach(diary -> dataScopeService.assertEditable(DataModuleEnum.DIARY, diary.getUserId()));
        portalDiaryMapper.deleteBatchByIds(distinctIds);
    }

    /**
     * 心情标识归一化（空/空白统一落为 {@code unknown}（不标记），
     * 保证库内值域始终为枚举声明的编码；非空必须是 {@link DiaryMoodEnum} 声明的编码）。
     *
     * @param mood 心情标识
     * @return 归一化后的心情标识
     */
    private String normalizeMood(String mood) {
        if (StrUtil.isBlank(mood)) {
            return DiaryMoodEnum.UNKNOWN.getCode();
        }
        if (ObjUtil.isNull(BaseEnum.fromCode(DiaryMoodEnum.class, mood))) {
            throw new BusinessException("心情标识不合法");
        }
        return mood;
    }

    /**
     * 分页查询内核 —— 门户与管理端分页共用的条件装配与分页执行。
     * <p>
     * 记录人/记录日期/心情为精确匹配，条件缺省时自动忽略；
     * 排序为记录日期降序 → id 降序；逻辑删除（del_flag）由全局配置自动追加过滤。
     *
     * @param page        分页参数
     * @param userId      记录人ID（精确匹配,可空）
     * @param diaryDate   记录日期（精确匹配,可空）
     * @param mood        心情标识（精确匹配,可空）
     * @param ownerFilter 行归属过滤值（数据范围策略；null 不过滤，管理端 self 时为当前用户ID）
     * @return 情侣日记 DTO 分页结果
     */
    private Page<DiaryDTO> doPage(BasePage page, Long userId, LocalDate diaryDate, String mood, Long ownerFilter) {
        Page<PortalDiary> entityPage = QueryChain.of(portalDiaryMapper)
                .where(PORTAL_DIARY.USER_ID.eq(userId, ObjUtil::isNotNull))
                .and(PORTAL_DIARY.DIARY_DATE.eq(diaryDate, ObjUtil::isNotNull))
                .and(PORTAL_DIARY.MOOD.eq(mood, StrUtil::isNotBlank))
                .and(PORTAL_DIARY.USER_ID.eq(ownerFilter, ObjUtil::isNotNull))
                .orderBy(PORTAL_DIARY.DIARY_DATE, false)
                .orderBy(PORTAL_DIARY.ID, false)
                .page(Page.of(page.getPageNumber(), page.getPageSize()));
        return diaryConverter.toPageDTO(entityPage);
    }


}

