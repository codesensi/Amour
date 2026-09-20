package cn.codesensi.amour.service;

import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.model.dto.FootprintChangeHiddenDTO;
import cn.codesensi.amour.model.dto.FootprintInsertDTO;
import cn.codesensi.amour.model.dto.FootprintDTO;
import cn.codesensi.amour.model.dto.FootprintPageDTO;
import cn.codesensi.amour.model.dto.FootprintUpdateDTO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 足迹地图 Service —— 门户下发与管理端维护共用（同一表域）。
 * <p>
 * Service 层以 DTO 进出：查询入参为分页参数 DTO，出参为 {@code FootprintItemDTO}，
 * 实体不跨越 Service 边界。
 *
 * @author codesensi
 * @since 1.0
 */
public interface FootprintService {

    /**
     * 门户足迹分页（免登录）。
     * <p>
     * 仅返回未删除的足迹，按到访日期升序 → id 升序（时间轴依旅程推进）；
     * 逻辑删除由全局配置自动过滤。
     *
     * @param page 分页参数（pageNumber/pageSize）
     * @return 足迹条目 DTO 分页
     */
    Page<FootprintDTO> pagePortal(BasePage page);

    /**
     * 门户足迹地图全量点集（免登录）。
     * <p>
     * 按到访日期升序 → id 升序（与门户分页排序一致），防御性上限 1000 条。
     *
     * @return 地图点集 DTO 列表
     */
    List<FootprintDTO> listMapPoints();

    /**
     * 管理端足迹分页（全量）。
     * <p>
     * 城市为模糊匹配，到访日期为闭区间范围过滤，条件缺省时自动忽略；
     * 排序与门户一致（到访日期升序 → id 升序）。
     *
     * @param pageDTO 分页查询参数 DTO
     * @return 足迹条目 DTO 分页
     */
    Page<FootprintDTO> pageAdmin(FootprintPageDTO pageDTO);

    /**
     * 新增足迹（主键由全局雪花配置生成）。
     *
     * @param insertDTO 新增参数
     */
    void insert(FootprintInsertDTO insertDTO);

    /**
     * 修改足迹（按 id 覆盖全部可编辑字段）。
     *
     * @param updateDTO 修改参数（id 必填）
     */
    void update(FootprintUpdateDTO updateDTO);

    /**
     * 修改足迹显隐（仅覆盖 hidden 字段）。
     *
     * @param changeHiddenDTO 显隐状态信息（id 必填）
     */
    void changeHidden(FootprintChangeHiddenDTO changeHiddenDTO);

    /**
     * 批量逻辑删除足迹。
     *
     * @param ids 足迹ID集合
     */
    void delete(List<Long> ids);

}
