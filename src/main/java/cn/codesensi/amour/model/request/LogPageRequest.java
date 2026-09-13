package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.core.BasePage;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 系统日志分页查询请求参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LogPageRequest extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名称(模糊匹配)
     */
    @Size(max = 128, message = "用户名称长度不能超过128")
    private String username;

    /**
     * 操作状态: 0-失败, 1-成功
     */
    private Integer status;

    /**
     * 日志类型集合(多选过滤;前端逗号分隔下发,空则不过滤)
     */
    private List<Integer> logTypes;

}
