package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 新增时间胶囊参数 DTO。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class TimeCapsuleInsertDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    private String title;

    /**
     * 信件内容
     */
    private String content;

    /**
     * 解锁时间（到点后门户可见全文）
     */
    private LocalDateTime openTime;

    /**
     * 显隐标识: 0-显示， 1-隐藏
     */
    private Integer hidden;

}
