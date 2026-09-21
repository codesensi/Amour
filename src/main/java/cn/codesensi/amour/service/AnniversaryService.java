package cn.codesensi.amour.service;

import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.model.dto.AnniversaryChangeHiddenDTO;
import cn.codesensi.amour.model.dto.AnniversaryDTO;
import cn.codesensi.amour.model.dto.AnniversaryInsertDTO;
import cn.codesensi.amour.model.dto.AnniversaryPageDTO;
import cn.codesensi.amour.model.dto.AnniversaryUpdateDTO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 门户纪念日 Service —— 门户下发与管理端维护共用。
 *
 * @author codesensi
 * @since 1.0
 */
public interface AnniversaryService {

    /**
     * 门户纪念日分页（免登录）。
     * <p>
     * 排序为下一次发生日升序（每年重复取今年/明年的同月日；
     * 一次性日期仅在未来计入，已过去垫底），与前端纪念日页倒计时口径一致。
     *
     * @param page 分页参数
     * @return 纪念日条目 DTO 分页结果
     */
    Page<AnniversaryDTO> pagePortal(BasePage page);

    /**
     * 查询下一次发生的纪念日（免登录，首页「下一个纪念日」卡片专用）。
     *
     * @return 最近一条纪念日条目 DTO；无数据时返回 null
     */
    AnniversaryDTO next();

    /**
     * 管理端纪念日分页（全量）。
     * <p>
     * 名称模糊匹配，显隐为精确匹配（条件缺省时自动忽略）；
     * 排序为下一次发生日升序 → id 升序（与门户口径一致）。
     *
     * @param pageDTO 分页查询参数 DTO
     * @return 纪念日条目 DTO 分页结果
     */
    Page<AnniversaryDTO> pageAdmin(AnniversaryPageDTO pageDTO);

    /**
     * 新增纪念日。
     *
     * @param insertDTO 新增参数 DTO
     */
    void insert(AnniversaryInsertDTO insertDTO);

    /**
     * 修改纪念日（按 id 覆盖全部可编辑字段）。
     *
     * @param updateDTO 修改参数 DTO
     */
    void update(AnniversaryUpdateDTO updateDTO);

    /**
     * 批量逻辑删除纪念日（任一 id 不存在时整批失败）。
     *
     * @param ids 纪念日ID集合
     */
    void delete(java.util.List<Long> ids);

    /**
     * 修改纪念日显隐。
     *
     * @param changeHiddenDTO 显隐状态信息
     */
    void changeHidden(AnniversaryChangeHiddenDTO changeHiddenDTO);

}
