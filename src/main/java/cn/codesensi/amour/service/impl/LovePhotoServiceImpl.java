package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.common.enums.HiddenEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.mapper.PortalLovePhotoMapper;
import cn.codesensi.amour.model.converter.LovePhotoConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.PortalLovePhoto;
import cn.codesensi.amour.service.LovePhotoService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.codesensi.amour.model.entity.table.PortalLovePhotoTableDef.PORTAL_LOVE_PHOTO;

/**
 * 恋爱相册照片 Service 实现 —— 门户下发与管理端维护共用。
 * <p>
 * 实体仅在层内流转：查询结果经转换器映射为 {@code LovePhotoDTO} 后返回，
 * 新增/修改接收 DTO 并在内部完成标签规范化与实体组装。
 *
 * @author codesensi
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class LovePhotoServiceImpl implements LovePhotoService {

    /**
     * 标签精确匹配条件：tags 以逗号分隔存储，FIND_IN_SET 逐值比对（私人相册数据量级，无需索引）
     */
    private static final String TAGS_CONTAINS = "FIND_IN_SET({0}, tags) > 0";

    private final PortalLovePhotoMapper portalLovePhotoMapper;
    private final LovePhotoConverter lovePhotoConverter;

    /**
     * 门户恋爱画册分页（免登录）。
     * <p>
     * 显隐口径固定为「仅显示」（hidden 强制过滤为 0，管理端维护口径之外的安全边界）；
     * 排序为 sort 升序 → id 升序（同 sort 保持稳定次序）。
     *
     * @param page 分页参数
     * @return 照片条目 DTO 分页结果
     */
    @Override
    public Page<LovePhotoDTO> pagePortal(BasePage page) {
        return doPage(page, null, null, HiddenEnum.SHOW.getCode());
    }

    /**
     * 管理端恋爱画册分页（全量，含隐藏照片）。
     * <p>
     * 文案为模糊匹配，标签在逗号分隔集合中精确匹配（FIND_IN_SET，
     * 数据量级为私人相册，无需标签倒排索引），条件缺省时自动忽略。
     *
     * @param pageDTO 分页查询参数 DTO
     * @return 照片条目 DTO 分页结果
     */
    @Override
    public Page<LovePhotoDTO> pageAdmin(LovePhotoPageDTO pageDTO) {
        return doPage(pageDTO, pageDTO.getCaption(), pageDTO.getTag(), pageDTO.getHidden());
    }

    /**
     * 分页查询内核 —— 门户与管理端分页共用的条件装配与分页执行。
     * <p>
     * 文案为模糊匹配，标签在逗号分隔集合中精确匹配（FIND_IN_SET，
     * 数据量级为私人相册，无需标签倒排索引），条件缺省时自动忽略；
     * 排序为 sort 升序 → id 升序；逻辑删除（del_flag）由全局配置自动追加过滤。
     *
     * @param page    分页参数
     * @param caption 文案（模糊匹配，可空）
     * @param tag     标签（逗号集合内精确匹配，可空）
     * @param hidden  显隐过滤（可空;门户固定传 0，管理端传筛选值或 null 查全量）
     * @return 照片条目 DTO 分页结果
     */
    private Page<LovePhotoDTO> doPage(BasePage page, String caption, String tag, Integer hidden) {
        QueryChain<PortalLovePhoto> chain = QueryChain.of(portalLovePhotoMapper)
                .where(PORTAL_LOVE_PHOTO.CAPTION.like(caption, StrUtil::isNotBlank))
                .and(PORTAL_LOVE_PHOTO.HIDDEN.eq(hidden, ObjUtil::isNotNull));
        if (StrUtil.isNotBlank(tag)) {
            chain.and(TAGS_CONTAINS, tag);
        }
        Page<PortalLovePhoto> entityPage = chain
                .orderBy(PORTAL_LOVE_PHOTO.SORT, true)
                .orderBy(PORTAL_LOVE_PHOTO.ID, true)
                .page(Page.of(page.getPageNumber(), page.getPageSize()));
        return lovePhotoConverter.toPageDTO(entityPage);
    }

    /**
     * 新增照片：显隐由表单传入，标签集合规范化后入库，主键由全局雪花配置生成
     * （后续显隐调整经 change-hidden 端点）。
     *
     * @param insertDTO 新增参数
     */
    @Override
    public void insert(LovePhotoInsertDTO insertDTO) {
        PortalLovePhoto entity = lovePhotoConverter.toEntity(insertDTO);
        entity.setTags(joinTags(insertDTO.getTags()));
        portalLovePhotoMapper.insert(entity);
    }

    /**
     * 修改照片：存在性校验后经 UpdateChain 显式逐列赋值更新（对齐 role 等既有 update 惯例，
     * 照片不存在时抛出业务异常）。显式赋值使 null/空串均真实写入（如清空标签），
     * 不受忽略 null 策略影响。
     *
     * @param updateDTO 修改参数（id 必填）
     */
    @Override
    public void update(LovePhotoUpdateDTO updateDTO) {
        PortalLovePhoto photo = QueryChain.of(portalLovePhotoMapper)
                .select(PORTAL_LOVE_PHOTO.ID)
                .where(PORTAL_LOVE_PHOTO.ID.eq(updateDTO.getId()))
                .one();
        if (ObjUtil.isNull(photo)) {
            throw new BusinessException("照片不存在");
        }
        UpdateChain.of(PortalLovePhoto.class)
                .set(PORTAL_LOVE_PHOTO.URL, updateDTO.getUrl())
                .set(PORTAL_LOVE_PHOTO.CAPTION, updateDTO.getCaption())
                .set(PORTAL_LOVE_PHOTO.DATE_TEXT, updateDTO.getDateText())
                .set(PORTAL_LOVE_PHOTO.TAGS, joinTags(updateDTO.getTags()))
                .set(PORTAL_LOVE_PHOTO.SORT, updateDTO.getSort())
                .where(PORTAL_LOVE_PHOTO.ID.eq(updateDTO.getId()))
                .update();
    }

    /**
     * 修改照片显隐（对齐 menu 等既有单列状态更新惯例：存在性校验 + 同状态幂等返回，
     * 仅覆盖 hidden 字段）。
     *
     * @param changeHiddenDTO 显隐状态信息
     */
    @Override
    public void changeHidden(LovePhotoChangeHiddenDTO changeHiddenDTO) {
        PortalLovePhoto photo = QueryChain.of(portalLovePhotoMapper)
                .select(PORTAL_LOVE_PHOTO.ID, PORTAL_LOVE_PHOTO.HIDDEN)
                .where(PORTAL_LOVE_PHOTO.ID.eq(changeHiddenDTO.getId()))
                .one();
        if (ObjUtil.isNull(photo)) {
            throw new BusinessException("照片不存在");
        }

        // 显隐一致时幂等返回
        if (changeHiddenDTO.getHidden().equals(photo.getHidden())) {
            return;
        }

        PortalLovePhoto entity = new PortalLovePhoto();
        entity.setId(changeHiddenDTO.getId());
        entity.setHidden(changeHiddenDTO.getHidden());
        portalLovePhotoMapper.update(entity);
    }

    /**
     * 批量逻辑删除照片（对齐 role/dictType 既有 delete 惯例：任一 id 不存在时整批失败）。
     *
     * @param ids 照片ID集合
     */
    @Override
    public void delete(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<Long> distinctIds = ids.stream().distinct().toList();
        List<Long> existingIds = QueryChain.of(portalLovePhotoMapper)
                .select(PORTAL_LOVE_PHOTO.ID)
                .where(PORTAL_LOVE_PHOTO.ID.in(distinctIds))
                .listAs(Long.class);
        if (existingIds.size() < distinctIds.size()) {
            throw new BusinessException("照片不存在");
        }
        portalLovePhotoMapper.deleteBatchByIds(distinctIds);
    }

    /**
     * 标签集合规范化：trim/去空/去重后以逗号拼接，拼接总长超限抛业务异常。
     *
     * @param tags 标签集合
     * @return 逗号分隔存储串；null 与空集合均返回 null（修改经 UpdateChain 显式写入
     * NULL 以清空标签；插入时忽略 null 由数据库默认值兜底）
     */
    private String joinTags(List<String> tags) {
        if (CollUtil.isEmpty(tags)) {
            return null;
        }
        String joined = tags.stream()
                .map(StrUtil::trim)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .reduce((a, b) -> a + "," + b)
                .orElse("");
        if (joined.length() > AppConst.MAX_LENGTH_64) {
            throw new BusinessException("照片标签拼接后长度不能超过" + AppConst.MAX_LENGTH_64 + "字符");
        }
        return joined;
    }

}
