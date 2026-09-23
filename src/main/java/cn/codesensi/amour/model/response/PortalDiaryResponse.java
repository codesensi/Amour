package cn.codesensi.amour.model.response;

import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 情侣日记门户响应结果（免登录下发，字段对齐前端双人分栏契约）。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class PortalDiaryResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 序列化为字符串避免前端精度丢失
    private Long id;

    /**
     * 记录人ID（双人日记按人分栏）
     */
    private Long userId;

    /**
     * 记录人用户名（服务层批量回填）
     */
    private String username;

    /**
     * 记录人昵称（服务层批量回填;前端展示链路 QQ 昵称 → 昵称 → 用户名）
     */
    private String nickname;

    /**
     * 记录人 QQ 号（服务层批量回填;已维护时前端走 QQ 头像链路）
     */
    private String qq;

    /**
     * 记录人头像（服务层批量回填;空则前端兜底图）
     */
    private String avatar;

    /**
     * 记录日期（yyyy-MM-dd）
     */
    private LocalDate diaryDate;

    /**
     * 心情标识（unknown-不标记,与 DiaryMoodEnum 对齐）
     */
    private String mood;

    /**
     * 日记内容
     */
    private String content;

}
