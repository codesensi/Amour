package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 管理端首页数据聚合 DTO。
 * <p>
 * 各字段均可为 null（对应模块暂无数据），由响应层按空状态渲染。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DashboardSummaryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 在一起天数（最早一条每年重复纪念日的间隔天数；无纪念日时为 null）
     */
    private Integer togetherDays;

    /**
     * 下一个纪念日（复用门户口径）
     */
    private AnniversaryDTO nextAnniversary;

    /**
     * 各模块条目计数
     */
    private ModuleCounts counts;

    /**
     * 恋爱清单进度
     */
    private LoveListProgress loveListProgress;

    /**
     * 最新一条留言（无留言时为 null）
     */
    private LatestMessage latestMessage;

    /**
     * 足迹到访城市数（去重，仅显示状态）
     */
    private Integer footprintsCityCount;

    /**
     * 恋爱画册最近照片地址（最多 9 张，按创建时间倒序；无照片时为 null）
     */
    private List<String> recentPhotos;

    /**
     * 各模块条目计数
     */
    @Data
    public static class ModuleCounts implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 纪念日条数
         */
        private Integer anniversaries;

        /**
         * 点点滴滴文章数
         */
        private Integer moments;

        /**
         * 恋爱画册照片数
         */
        private Integer photos;

        /**
         * 留言簿留言数
         */
        private Integer messages;

        /**
         * 情侣日志篇数
         */
        private Integer diaries;

        /**
         * 时间胶囊数
         */
        private Integer timeCapsules;

        /**
         * 足迹条数
         */
        private Integer footprints;
    }

    /**
     * 恋爱清单进度
     */
    @Data
    public static class LoveListProgress implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 清单总条数
         */
        private Integer total;

        /**
         * 已完成数
         */
        private Integer done;
    }

    /**
     * 最新留言
     */
    @Data
    public static class LatestMessage implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 访客昵称
         */
        private String nickname;

        /**
         * 留言内容
         */
        private String content;

        /**
         * 留言时间（yyyy-MM-dd HH:mm）
         */
        private String createTime;
    }

}
