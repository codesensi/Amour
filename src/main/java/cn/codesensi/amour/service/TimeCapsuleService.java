package cn.codesensi.amour.service;

import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.model.dto.TimeCapsuleChangeHiddenDTO;
import cn.codesensi.amour.model.dto.TimeCapsuleInsertDTO;
import cn.codesensi.amour.model.dto.TimeCapsuleDTO;
import cn.codesensi.amour.model.dto.TimeCapsulePageDTO;
import cn.codesensi.amour.model.dto.TimeCapsuleUpdateDTO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 时间胶囊 Service —— 门户下发与管理端维护共用（同一表域）。
 * <p>
 * Service 层以 DTO 进出：查询入参为分页参数 DTO，出参为 {@code TimeCapsuleItemDTO}，
 * 实体不跨越 Service 边界。
 *
 * @author codesensi
 * @since 1.0
 */
public interface TimeCapsuleService {

    /**
     * 门户时间胶囊分页（免登录）。
     * <p>
     * 仅返回显隐为「显示」的胶囊，按解锁时间升序 → id 升序；
     * 未到解锁时间的记录 content 置空下发（防抓包剧透），unlocked 由服务层判定。
     *
     * @param page 分页参数（pageNumber/pageSize）
     * @return 时间胶囊 DTO 分页
     */
    Page<TimeCapsuleDTO> pagePortal(BasePage page);

    /**
     * 管理端时间胶囊分页（全量，含隐藏项与未解锁项）。
     * <p>
     * 标题为模糊匹配，显隐为精确匹配，条件缺省时自动忽略。
     *
     * @param pageDTO 分页查询参数 DTO
     * @return 时间胶囊 DTO 分页
     */
    Page<TimeCapsuleDTO> pageAdmin(TimeCapsulePageDTO pageDTO);

    /**
     * 新增时间胶囊。
     *
     * @param insertDTO 新增参数
     */
    void insert(TimeCapsuleInsertDTO insertDTO);

    /**
     * 修改时间胶囊（按 id 覆盖全部可编辑字段；显隐单独走 change-hidden）。
     *
     * @param updateDTO 修改参数（id 必填）
     */
    void update(TimeCapsuleUpdateDTO updateDTO);

    /**
     * 修改时间胶囊显隐（存在性校验 + 同状态幂等返回，仅覆盖 hidden 字段）。
     *
     * @param changeHiddenDTO 显隐状态信息
     */
    void changeHidden(TimeCapsuleChangeHiddenDTO changeHiddenDTO);

    /**
     * 批量逻辑删除时间胶囊。
     *
     * @param ids 胶囊ID集合
     */
    void delete(List<Long> ids);

}
