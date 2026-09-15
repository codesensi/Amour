package cn.codesensi.amour.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知条目 DTO —— 面向通知查询结果的数据传输对象（当前用户视角）。
 * <p>
 * read（是否已读）由服务层按当前登录用户组装；控制层经转换器映射为 NoticeResponse。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class NoticeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 通知ID
     */
    private Long id;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 当前用户是否已读
     */
    private Boolean read;

}
