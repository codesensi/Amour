package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.common.enums.HiddenEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.mapper.PortalLoveListMapper;
import cn.codesensi.amour.model.converter.LoveListConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.PortalLoveList;
import cn.codesensi.amour.service.LoveListService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.codesensi.amour.model.entity.table.PortalLoveListTableDef.PORTAL_LOVE_LIST;

/**
 * 恋爱清单 Service 实现 —— 门户下发与管理端维护共用。
 * <p>
 * 实体仅在层内流转：查询结果经转换器映射为 {@code LoveListItemDTO} 后返回，
 * 新增/修改接收 DTO 并在内部完成实体组装；完成状态与显隐
 * 并入修改表单整体维护（菜单未预留独立状态切换端点）。
 *
 * @author codesensi
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class LoveListServiceImpl implements LoveListService {

    private final PortalLoveListMapper portalLoveListMapper;

    private final LoveListConverter loveListConverter;

    /**
     * 门户恋爱清单分页（免登录）。
     * <p>
     * 显隐口径固定为「仅显示」（hidden 强制过滤为 0，管理端维护口径之外的安全边界）；
     * 排序为 sort 升序 → id 升序（同 sort 保持稳定次序）。
     *
     * @param page 分页参数
     * @return 清单项 DTO 分页结果
     */
    @Override
    public Page<LoveListItemDTO> pagePortal(BasePage page) {
        return doPage(page, null, null, HiddenEnum.SHOW.getCode());
    }

    /**
     * 管理端恋爱清单分页（全量，含隐藏项）。
     * <p>
     * 内容为模糊匹配，完成状态与显隐为精确匹配，条件缺省时自动忽略。
     *
     * @param pageDTO 分页查询参数 DTO
     * @return 清单项 DTO 分页结果
     */
    @Override
    public Page<LoveListItemDTO> pageAdmin(LoveListPageDTO pageDTO) {
        return doPage(pageDTO, pageDTO.getContent(), pageDTO.getDone(), pageDTO.getHidden());
    }

    /**
     * 分页查询内核 —— 门户与管理端分页共用的条件装配与分页执行。
     * <p>
     * 内容为模糊匹配，完成状态与显隐为精确匹配，条件缺省时自动忽略；
     * 排序为 sort 升序 → id 升序；逻辑删除（del_flag）由全局配置自动追加过滤。
     *
     * @param page    分页参数
     * @param content 清单内容（模糊匹配，可空）
     * @param done    完成状态过滤（可空;门户固定传 null，管理端传筛选值）
     * @param hidden  显隐过滤（可空;门户固定传 0，管理端传筛选值或 null 查全量）
     * @return 清单项 DTO 分页结果
     */
    private Page<LoveListItemDTO> doPage(BasePage page, String content, Integer done, Integer hidden) {
        Page<PortalLoveList> entityPage = QueryChain.of(portalLoveListMapper)
                .where(PORTAL_LOVE_LIST.CONTENT.like(content, StrUtil::isNotBlank))
                .and(PORTAL_LOVE_LIST.DONE.eq(done, ObjUtil::isNotNull))
                .and(PORTAL_LOVE_LIST.HIDDEN.eq(hidden, ObjUtil::isNotNull))
                .orderBy(PORTAL_LOVE_LIST.SORT, true)
                .orderBy(PORTAL_LOVE_LIST.ID, true)
                .page(Page.of(page.getPageNumber(), page.getPageSize()));
        return loveListConverter.toPageDTO(entityPage);
    }

    /**
     * 新增清单项：完成状态与显隐由表单传入，主键由全局雪花配置生成。
     *
     * @param insertDTO 新增参数
     */
    @Override
    public void insert(LoveListInsertDTO insertDTO) {
        PortalLoveList entity = loveListConverter.toEntity(insertDTO);
        portalLoveListMapper.insert(entity);
    }

    /**
     * 修改清单项：存在性校验后经 UpdateChain 显式逐列赋值更新（对齐 love-photo 等既有
     * update 惯例，清单项不存在时抛出业务异常）。显式赋值使 null/空串均真实写入
     * （如清空纪念照），不受忽略 null 策略影响；显隐不经本方法维护（单独走 {@link #changeHidden}）。
     *
     * @param updateDTO 修改参数（id 必填）
     */
    @Override
    public void update(LoveListUpdateDTO updateDTO) {
        PortalLoveList loveList = QueryChain.of(portalLoveListMapper)
                .select(PORTAL_LOVE_LIST.ID)
                .where(PORTAL_LOVE_LIST.ID.eq(updateDTO.getId()))
                .one();
        if (ObjUtil.isNull(loveList)) {
            throw new BusinessException("清单项不存在");
        }
        UpdateChain.of(PortalLoveList.class)
                .set(PORTAL_LOVE_LIST.CONTENT, updateDTO.getContent())
                .set(PORTAL_LOVE_LIST.DONE, updateDTO.getDone())
                .set(PORTAL_LOVE_LIST.PHOTO, updateDTO.getPhoto())
                .set(PORTAL_LOVE_LIST.SORT, updateDTO.getSort())
                .where(PORTAL_LOVE_LIST.ID.eq(updateDTO.getId()))
                .update();
    }

    /**
     * 修改清单项显隐（对齐 love-photo 既有单列状态更新惯例：存在性校验 + 同状态幂等返回，
     * 仅覆盖 hidden 字段）。
     *
     * @param changeHiddenDTO 显隐状态信息
     */
    @Override
    public void changeHidden(LoveListChangeHiddenDTO changeHiddenDTO) {
        PortalLoveList loveList = QueryChain.of(portalLoveListMapper)
                .select(PORTAL_LOVE_LIST.ID, PORTAL_LOVE_LIST.HIDDEN)
                .where(PORTAL_LOVE_LIST.ID.eq(changeHiddenDTO.getId()))
                .one();
        if (ObjUtil.isNull(loveList)) {
            throw new BusinessException("清单项不存在");
        }

        // 显隐一致时幂等返回
        if (changeHiddenDTO.getHidden().equals(loveList.getHidden())) {
            return;
        }

        PortalLoveList entity = new PortalLoveList();
        entity.setId(changeHiddenDTO.getId());
        entity.setHidden(changeHiddenDTO.getHidden());
        portalLoveListMapper.update(entity);
    }

    /**
     * 批量逻辑删除清单项（对齐 love-photo/message 既有 delete 惯例：任一 id 不存在时整批失败）。
     *
     * @param ids 清单项ID集合
     */
    @Override
    public void delete(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<Long> distinctIds = ids.stream().distinct().toList();
        List<Long> existingIds = QueryChain.of(portalLoveListMapper)
                .select(PORTAL_LOVE_LIST.ID)
                .where(PORTAL_LOVE_LIST.ID.in(distinctIds))
                .listAs(Long.class);
        if (existingIds.size() < distinctIds.size()) {
            throw new BusinessException("清单项不存在");
        }
        portalLoveListMapper.deleteBatchByIds(distinctIds);
    }

}
