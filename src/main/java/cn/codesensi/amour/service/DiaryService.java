package cn.codesensi.amour.service;

import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.model.dto.DiaryDTO;
import cn.codesensi.amour.model.dto.DiaryInsertDTO;
import cn.codesensi.amour.model.dto.DiaryPageDTO;
import cn.codesensi.amour.model.dto.DiaryUpdateDTO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 情侣日记 Service —— 门户下发与管理端维护共用（同一表域）。
 * <p>
 * Service 层以 DTO 进出：查询入参为分页参数 DTO，出参为 {@code DiaryDTO}，
 * 实体不跨越 Service 边界。
 *
 * @author codesensi
 * @since 1.0
 */
public interface DiaryService {

    /**
     * 门户情侣日记分页（免登录）。
     * <p>
     * 按记录日期降序 → id 降序（最近的日记在前），
     * 记录人昵称/头像由服务层批量回填。
     *
     * @param page 分页参数（pageNumber/pageSize）
     * @return 情侣日记 DTO 分页
     */
    Page<DiaryDTO> pagePortal(BasePage page);

    /**
     * 管理端情侣日记分页（全量）。
     * <p>
     * 记录人/记录日期/心情为精确匹配，条件缺省时自动忽略；
     * 回填记录人展示信息与审计用户名。
     *
     * @param pageDTO 分页查询参数 DTO
     * @return 情侣日记 DTO 分页
     */
    Page<DiaryDTO> pageAdmin(DiaryPageDTO pageDTO);

    /**
     * 新增情侣日记（记录人取当前登录人）。
     *
     * @param insertDTO 新增参数
     */
    void insert(DiaryInsertDTO insertDTO);

    /**
     * 修改情侣日记（按 id 覆盖可编辑字段;记录人归属不可变）。
     *
     * @param updateDTO 修改参数（id 必填）
     */
    void update(DiaryUpdateDTO updateDTO);

    /**
     * 批量逻辑删除情侣日记。
     *
     * @param ids 日记ID集合
     */
    void delete(List<Long> ids);

}
