package cn.codesensi.amour.service;

import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.model.dto.LoveListItemDTO;
import cn.codesensi.amour.model.dto.LoveListChangeHiddenDTO;
import cn.codesensi.amour.model.dto.LoveListPageDTO;
import cn.codesensi.amour.model.dto.LoveListInsertDTO;
import cn.codesensi.amour.model.dto.LoveListUpdateDTO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 恋爱清单 Service —— 门户下发与管理端维护共用（同一表域）。
 * <p>
 * Service 层以 DTO 进出：查询入参为分页参数 DTO，出参为 {@code LoveListItemDTO}，
 * 实体不跨越 Service 边界。
 *
 * @author codesensi
 * @since 1.0
 */
public interface LoveListService {

    /**
     * 门户恋爱清单分页（免登录）。
     * <p>
     * 仅返回显隐为「显示」的清单项，按 sort 升序 → id 升序；逻辑删除由全局配置自动过滤。
     *
     * @param page 分页参数（pageNumber/pageSize）
     * @return 清单项 DTO 分页
     */
    Page<LoveListItemDTO> pagePortal(BasePage page);

    /**
     * 管理端恋爱清单分页（全量，含隐藏项）。
     * <p>
     * 内容为模糊匹配，完成状态与显隐为精确匹配，条件缺省时自动忽略。
     *
     * @param pageDTO 分页查询参数 DTO
     * @return 清单项 DTO 分页
     */
    Page<LoveListItemDTO> pageAdmin(LoveListPageDTO pageDTO);

    /**
     * 新增清单项。
     *
     * @param insertDTO 新增参数
     */
    void insert(LoveListInsertDTO insertDTO);

    /**
     * 修改清单项（按 id 覆盖全部可编辑字段，完成状态与纪念照随表单维护；显隐单独走 change-hidden）。
     *
     * @param updateDTO 修改参数（id 必填）
     */
    void update(LoveListUpdateDTO updateDTO);

    /**
     * 修改清单项显隐（存在性校验 + 同状态幂等返回，仅覆盖 hidden 字段）。
     *
     * @param changeHiddenDTO 显隐状态信息
     */
    void changeHidden(LoveListChangeHiddenDTO changeHiddenDTO);

    /**
     * 批量逻辑删除清单项。
     *
     * @param ids 清单项ID集合
     */
    void delete(List<Long> ids);

}
