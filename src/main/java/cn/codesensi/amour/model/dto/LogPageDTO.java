package cn.codesensi.amour.model.dto;

import cn.codesensi.amour.common.core.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 系统日志分页查询参数
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LogPageDTO extends BasePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名称(模糊匹配)
     */
    private String username;

    /**
     * 操作状态: 0-失败, 1-成功
     */
    private Integer status;

    /**
     * 日志类型集合(多选过滤;空则不过滤,取值范围见 LogTypeEnum)
     */
    private List<Integer> logTypes;

}
