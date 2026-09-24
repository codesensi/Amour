package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 修改时间胶囊参数 DTO（id 必填；显隐不经本 DTO 维护，单独走 change-hidden）。
 * <p>
 * 不继承新增 DTO：修改表单在胶囊解锁后内容只读，字段独立声明便于按形态演进。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class TimeCapsuleUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 胶囊ID
     */
    private Long id;

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

}
