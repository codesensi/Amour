package cn.codesensi.amour.common.core;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用分页请求参数 —— 各分页查询 Request/DTO 继承复用。
 * <p>
 * 页码与每页条数均带缺省值(1 与 20),与前端默认值保持一致;
 * 查询参数绑定时缺参不覆盖字段初始值,天然实现默认分页。
 *
 * @author codesensi
 * @since 2026-09-04
 */
@Data
public class BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 页码(从 1 开始,缺省为 1)
     */
    @Min(value = 1, message = "页码最小值为1")
    private Integer pageNumber = 1;

    /**
     * 每页条数(最大 100,缺省为 20)
     */
    @Min(value = 1, message = "每页条数最小值为1")
    @Max(value = 500, message = "每页条数最大值为500")
    private Integer pageSize = 20;

}
