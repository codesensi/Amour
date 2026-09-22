package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理端首页最近回忆时间线条目 DTO。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DashboardTimelineItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 条目类型（与来源表对应）
     */
    private TimelineItemType type;

    /**
     * 条目类型
     */
    public enum TimelineItemType {
        /**
         * 恋爱画册照片
         */
        photos,
        /**
         * 点点滴滴文章
         */
        moments,
        /**
         * 情侣日志
         */
        diary
    }

    /**
     * 时间（yyyy-MM-dd HH:mm，取记录创建时间）
     */
    private String time;

    /**
     * 标题（文章标题/照片文案/日志为固定前缀+日期）
     */
    private String title;

    /**
     * 内容摘要（照片类型为 null）
     */
    private String content;

    /**
     * 照片地址（仅画册条目有；其余为 null）
     */
    private String imageUrl;

}
