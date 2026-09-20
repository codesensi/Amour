package cn.codesensi.amour.service;

import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.model.dto.*;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 恋爱相册照片 Service —— 门户下发与管理端维护共用（同一表域）。
 * <p>
 * Service 层以 DTO 进出：查询入参为分页参数 DTO，出参为 {@code LovePhotoDTO}，
 * 实体不跨越 Service 边界。
 *
 * @author codesensi
 * @since 1.0
 */
public interface LovePhotoService {

    /**
     * 门户恋爱画册分页（免登录）。
     * <p>
     * 仅返回显隐为「显示」的照片，按 sort 升序 → id 升序；逻辑删除由全局配置自动过滤。
     *
     * @param page 分页参数（pageNumber/pageSize）
     * @return 照片条目 DTO 分页
     */
    Page<LovePhotoDTO> pagePortal(BasePage page);

    /**
     * 门户恋爱画册封面照片（免登录）。
     * <p>
     * 取显隐为「显示」的照片中 sort 首位（sort 升序 → id 升序，与门户分页排序一致）。
     *
     * @return 封面照片条目 DTO；画册为空时为 null
     */
    LovePhotoDTO getPortalCover();

    /**
     * 管理端恋爱画册分页（全量，含隐藏照片）。
     * <p>
     * 文案为模糊匹配，标签在逗号分隔集合中精确匹配（FIND_IN_SET），
     * 显隐为精确匹配，条件缺省时自动忽略。
     *
     * @param pageDTO 分页查询参数 DTO
     * @return 照片条目 DTO 分页
     */
    Page<LovePhotoDTO> pageAdmin(LovePhotoPageDTO pageDTO);

    /**
     * 新增照片（标签集合规范化为逗号分隔存储）。
     *
     * @param insertDTO 新增参数
     */
    void insert(LovePhotoInsertDTO insertDTO);

    /**
     * 修改照片（按 id 覆盖全部可编辑字段，标签集合同新增规范化）。
     *
     * @param updateDTO 修改参数（id 必填）
     */
    void update(LovePhotoUpdateDTO updateDTO);

    /**
     * 修改照片显隐（存在性校验 + 同状态幂等返回，仅覆盖 hidden 字段）。
     *
     * @param changeHiddenDTO 显隐状态信息
     */
    void changeHidden(LovePhotoChangeHiddenDTO changeHiddenDTO);

    /**
     * 批量逻辑删除照片。
     *
     * @param ids 照片ID集合
     */
    void delete(List<Long> ids);

}
