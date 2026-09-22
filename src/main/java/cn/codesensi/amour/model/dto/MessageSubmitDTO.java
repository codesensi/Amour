package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 提交留言参数 DTO。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class MessageSubmitDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * QQ 号（头像快照来源）
     */
    private String qq;

    /**
     * 访客昵称
     */
    private String name;

    /**
     * 留言内容
     */
    private String text;

}
