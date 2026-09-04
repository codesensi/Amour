package cn.codesensi.amour.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * QQ 信息查询请求 —— 门户免登录场景的 QQ 资料查询入参。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class QqInfoRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * QQ 号（6~12 位数字）
     */
    @NotBlank(message = "QQ号码不能为空")
    @Pattern(regexp = "^[0-9]{6,12}$", message = "QQ号码格式错误，请输入6-12位数字")
    private String qq;

}
