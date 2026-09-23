package cn.codesensi.amour.model.response;

import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 时间胶囊门户响应结果（免登录下发）。
 * <p>
 * content 仅在解锁时间到达后下发，未解锁记录为 null（防抓包剧透），
 * 由服务层按当前时间判定并以 {@code unlocked} 标识下发。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class PortalTimeCapsuleResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 信件内容（未到解锁时间为 null）
     */
    private String content;

    /**
     * 解锁时间（经全局 Jackson 配置序列化为 yyyy-MM-dd HH:mm:ss）
     */
    private LocalDateTime openTime;

    /**
     * 是否已解锁: true-已解锁(可见全文), false-未解锁(仅封存卡)
     */
    private Boolean unlocked;

    /**
     * 封存时间
     */
    private LocalDateTime createTime;

}
