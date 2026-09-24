package cn.codesensi.amour.service;

import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.model.dto.MomentsChangeStatusDTO;
import cn.codesensi.amour.model.dto.MomentsDTO;
import cn.codesensi.amour.model.dto.MomentsInsertDTO;
import cn.codesensi.amour.model.dto.MomentsPageDTO;
import cn.codesensi.amour.model.dto.MomentsHistoryDTO;
import cn.codesensi.amour.model.dto.MomentsUpdateDTO;
import com.mybatisflex.core.paginate.Page;
import java.util.List;

/**
 * 点点滴滴 Service —— 门户下发与管理端维护共用（同一表域）。
 * <p>
 * Service 层以 DTO 进出：查询入参为分页参数 DTO，出参为 {@code MomentsDTO}，
 * 实体不跨越 Service 边界。
 *
 * @author codesensi
 * @since 1.0
 */
public interface MomentsService {

    /**
     * 门户点点滴滴分页（免登录）。
     * <p>
     * 仅下发显示状态的文章，按排序升序 → 记录日期降序 → id 降序，
     * 作者展示信息由服务层批量回填。
     *
     * @param page 分页参数（pageNumber/pageSize）
     * @return 点点滴滴 DTO 分页
     */
    Page<MomentsDTO> pagePortal(BasePage page);

    /**
     * 管理端点点滴滴分页（全量）。
     * <p>
     * 标题/分类/状态为条件匹配，条件缺省时自动忽略；
     * 回填作者展示信息与审计用户名。
     *
     * @param pageDTO 分页查询参数 DTO
     * @return 点点滴滴 DTO 分页
     */
    Page<MomentsDTO> pageAdmin(MomentsPageDTO pageDTO);

    /**
     * 查询单篇文章详情（免登录,仅显示状态）。
     *
     * @param id 文章ID
     * @return 点点滴滴 DTO;不存在或为隐藏状态时返回 null
     */
    MomentsDTO detail(Long id);

    /**
     * 历史分类/标签建议（管理端表单自动补全）。
     *
     * @return 去重后的分类与标签集合
     */
    MomentsHistoryDTO history();

    /**
     * 修改文章显示状态（仅覆盖 status 字段,对齐足迹等既有单列状态更新惯例）。
     *
     * @param changeStatusDTO 状态信息
     */
    void changeStatus(MomentsChangeStatusDTO changeStatusDTO);

    /**
     * 新增点点滴滴文章（作者取当前登录人）。
     *
     * @param insertDTO 新增参数
     */
    void insert(MomentsInsertDTO insertDTO);

    /**
     * 修改点点滴滴文章（按 id 覆盖可编辑字段;作者归属不可变）。
     *
     * @param updateDTO 修改参数（id 必填）
     */
    void update(MomentsUpdateDTO updateDTO);

    /**
     * 批量逻辑删除点点滴滴文章。
     *
     * @param ids 文章ID集合
     */
    void delete(List<Long> ids);

}
