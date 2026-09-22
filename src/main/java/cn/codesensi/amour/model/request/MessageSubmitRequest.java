package cn.codesensi.amour.model.request;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.common.consts.RegexConst;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 提交留言请求 —— 门户免登录场景的访客留言入参。
 * <p>
 * 头像不在请求内：由服务端按 {@code qq} 快照 QQ 头像（fail-soft，取不到时入库为空，
 * 由前端本地兜底图兜底）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class MessageSubmitRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * QQ 号（6~12 位数字；头像快照来源）
     */
    @NotBlank(message = "QQ号码不能为空")
    @Pattern(regexp = RegexConst.EMPTY_OR_6_12_DIGITS, message = "QQ号码格式错误，请输入6-12位数字")
    private String qq;

    /**
     * 访客昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Size(max = AppConst.MAX_LENGTH_64, message = "昵称长度不能超过" + AppConst.MAX_LENGTH_64)
    private String name;

    /**
     * 留言内容
     */
    @NotBlank(message = "留言内容不能为空")
    @Size(max = AppConst.MAX_LENGTH_1024, message = "留言内容长度不能超过" + AppConst.MAX_LENGTH_1024)
    private String text;

}
