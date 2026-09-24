package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.support.AuditUserFiller;
import cn.codesensi.amour.common.support.AuthorInfoFiller;
import cn.codesensi.amour.mapper.PortalMomentsMapper;
import cn.codesensi.amour.model.converter.MomentsConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.PortalMoments;
import cn.codesensi.amour.service.MomentsService;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static cn.codesensi.amour.model.entity.table.PortalMomentsTableDef.PORTAL_MOMENTS;

/**
 * 点点滴滴 ServiceImpl —— 门户下发与管理端维护共用。
 * <p>
 * 列表排序为 sort 升序 → 记录日期降序 → id 降序（置顶优先,新文章在前）；
 * 作者展示信息（{@code AuthorInfoFiller}）与审计用户名（{@code AuditUserFiller}）由服务层批量回填。
 *
 * @author codesensi
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class MomentsServiceImpl implements MomentsService {

    private final PortalMomentsMapper portalMomentsMapper;

    private final AuthorInfoFiller authorInfoFiller;

    private final MomentsConverter momentsConverter;

    private final AuditUserFiller auditUserFiller;

    /**
     * 状态:显示（与表注释及 HiddenEnum 方向一致）。
     */
    private static final int STATUS_VISIBLE = 0;

    /**
     * 门户点点滴滴分页（免登录）：仅显示状态文章。
     *
     * @param page 分页参数
     * @return 点点滴滴 DTO 分页
     */
    @Override
    public Page<MomentsDTO> pagePortal(BasePage page) {
        Page<PortalMoments> entityPage = QueryChain.of(portalMomentsMapper)
                .where(PORTAL_MOMENTS.STATUS.eq(STATUS_VISIBLE))
                .orderBy(PORTAL_MOMENTS.SORT, true)
                .orderBy(PORTAL_MOMENTS.RECORD_DATE, false)
                .orderBy(PORTAL_MOMENTS.ID, false)
                .page(Page.of(page.getPageNumber(), page.getPageSize()));
        Page<MomentsDTO> itemPage = momentsConverter.toPageDTO(entityPage);
        authorInfoFiller.fill(itemPage.getRecords());
        return itemPage;
    }

    /**
     * 管理端点点滴滴分页（全量）：标题模糊 + 分类/状态精确,条件缺省自动忽略。
     *
     * @param pageDTO 分页查询参数 DTO
     * @return 点点滴滴 DTO 分页
     */
    @Override
    public Page<MomentsDTO> pageAdmin(MomentsPageDTO pageDTO) {
        Page<PortalMoments> entityPage = QueryChain.of(portalMomentsMapper)
                .where(PORTAL_MOMENTS.TITLE.like(pageDTO.getTitle(), StrUtil::isNotBlank))
                .and(PORTAL_MOMENTS.CATEGORY.eq(pageDTO.getCategory(), StrUtil::isNotBlank))
                .and(PORTAL_MOMENTS.STATUS.eq(pageDTO.getStatus(), ObjUtil::isNotNull))
                .orderBy(PORTAL_MOMENTS.SORT, true)
                .orderBy(PORTAL_MOMENTS.RECORD_DATE, false)
                .orderBy(PORTAL_MOMENTS.ID, false)
                .page(Page.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
        Page<MomentsDTO> itemPage = momentsConverter.toPageDTO(entityPage);
        authorInfoFiller.fill(itemPage.getRecords());
        auditUserFiller.fill(itemPage.getRecords());
        return itemPage;
    }

    /**
     * 查询单篇文章详情（免登录,仅显示状态）。
     *
     * @param id 文章ID
     * @return 点点滴滴 DTO;不存在或为隐藏状态时返回 null
     */
    @Override
    public MomentsDTO detail(Long id) {
        PortalMoments entity = QueryChain.of(portalMomentsMapper)
                .where(PORTAL_MOMENTS.ID.eq(id))
                .and(PORTAL_MOMENTS.STATUS.eq(STATUS_VISIBLE))
                .one();
        if (ObjUtil.isNull(entity)) {
            return null;
        }
        MomentsDTO itemDTO = momentsConverter.toDTO(entity);
        authorInfoFiller.fill(List.of(itemDTO));
        return itemDTO;
    }

    /**
     * 历史分类/标签建议：全表取非空分类与标签,分类去重;标签按逗号拆分后去重（保持出现顺序）。
     *
     * @return 去重后的分类与标签列表
     */
    @Override
    public MomentsHistoryDTO history() {
        // 主键列必须一并查出:仅取可空列时,两列皆为 NULL 的行会被 MyBatis 映射为 null 实体
        List<PortalMoments> rows = QueryChain.of(portalMomentsMapper)
                .select(PORTAL_MOMENTS.ID, PORTAL_MOMENTS.CATEGORY, PORTAL_MOMENTS.TAGS)
                .list();
        Set<String> categorySet = new LinkedHashSet<>();
        Set<String> tagSet = new LinkedHashSet<>();
        rows.forEach(row -> {
            if (ObjUtil.isNull(row)) {
                return;
            }
            if (StrUtil.isNotBlank(row.getCategory())) {
                categorySet.add(row.getCategory());
            }
            if (StrUtil.isNotBlank(row.getTags())) {
                Arrays.stream(row.getTags().split(","))
                        .map(String::trim)
                        .filter(StrUtil::isNotBlank)
                        .forEach(tagSet::add);
            }
        });
        MomentsHistoryDTO history = new MomentsHistoryDTO();
        history.setCategories(List.copyOf(categorySet));
        history.setTags(List.copyOf(tagSet));
        return history;
    }

    /**
     * 修改文章显示状态（对齐纪念日等既有单列状态更新惯例：存在性校验 + 同状态幂等返回，
     * 仅覆盖 status 字段）。
     *
     * @param changeStatusDTO 状态信息
     */
    @Override
    public void changeStatus(MomentsChangeStatusDTO changeStatusDTO) {
        PortalMoments entity = QueryChain.of(portalMomentsMapper)
                .select(PORTAL_MOMENTS.ID, PORTAL_MOMENTS.STATUS)
                .where(PORTAL_MOMENTS.ID.eq(changeStatusDTO.getId()))
                .one();
        if (ObjUtil.isNull(entity)) {
            throw new BusinessException("文章不存在");
        }

        // 状态一致时幂等返回
        if (changeStatusDTO.getStatus().equals(entity.getStatus())) {
            return;
        }

        PortalMoments item = new PortalMoments();
        item.setId(changeStatusDTO.getId());
        item.setStatus(changeStatusDTO.getStatus());
        portalMomentsMapper.update(item);
    }

    /**
     * 新增点点滴滴文章（作者取当前登录人）。
     *
     * @param insertDTO 新增参数
     */
    @Override
    public void insert(MomentsInsertDTO insertDTO) {
        PortalMoments entity = momentsConverter.toEntity(insertDTO);
        entity.setUserId(StpUtil.getLoginIdAsLong());
        portalMomentsMapper.insert(entity);
    }

    /**
     * 修改点点滴滴文章：存在性校验后经 UpdateChain 显式逐列赋值更新（对齐日记等既有
     * update 惯例,文章不存在时抛出业务异常）。作者归属不可变（user_id 不参与更新）。
     *
     * @param updateDTO 修改参数（id 必填）
     */
    @Override
    public void update(MomentsUpdateDTO updateDTO) {
        PortalMoments moments = QueryChain.of(portalMomentsMapper)
                .select(PORTAL_MOMENTS.ID)
                .where(PORTAL_MOMENTS.ID.eq(updateDTO.getId()))
                .one();
        if (ObjUtil.isNull(moments)) {
            throw new BusinessException("文章不存在");
        }
        UpdateChain.of(PortalMoments.class)
                .set(PORTAL_MOMENTS.TITLE, updateDTO.getTitle())
                .set(PORTAL_MOMENTS.CONTENT, updateDTO.getContent())
                .set(PORTAL_MOMENTS.RECORD_DATE, updateDTO.getRecordDate())
                .set(PORTAL_MOMENTS.SORT, updateDTO.getSort())
                .set(PORTAL_MOMENTS.CATEGORY, updateDTO.getCategory())
                .set(PORTAL_MOMENTS.TAGS, updateDTO.getTags())
                .where(PORTAL_MOMENTS.ID.eq(updateDTO.getId()))
                .update();
    }

    /**
     * 批量逻辑删除点点滴滴文章（对齐既有 delete 惯例：任一 id 不存在时整批失败）。
     *
     * @param ids 文章ID集合
     */
    @Override
    public void delete(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<Long> distinctIds = ids.stream().distinct().toList();
        List<Long> existingIds = QueryChain.of(portalMomentsMapper)
                .select(PORTAL_MOMENTS.ID)
                .where(PORTAL_MOMENTS.ID.in(distinctIds))
                .listAs(Long.class);
        if (existingIds.size() < distinctIds.size()) {
            throw new BusinessException("文章不存在");
        }
        portalMomentsMapper.deleteBatchByIds(distinctIds);
    }

}

