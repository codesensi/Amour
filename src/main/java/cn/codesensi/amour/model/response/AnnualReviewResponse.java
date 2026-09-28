package cn.codesensi.amour.model.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 年度恋爱回顾响应结果。
 * <p>
 * 各字段均可为 null 或空集合（对应模块当年暂无数据），由前端按空状态渲染。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class AnnualReviewResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 统计年份
     */
    private Integer year;

    /**
     * 当年情侣日志篇数
     */
    private Integer diaryCount;

    /**
     * 当年点点滴滴文章数
     */
    private Integer momentsCount;

    /**
     * 当年恋爱画册照片数
     */
    private Integer photoCount;

    /**
     * 当年足迹到访次数
     */
    private Integer footprintCount;

    /**
     * 当年首次到访的城市
     */
    private List<String> newCities;

    /**
     * 出现次数最多的心情标识（sunny/rainy/starry；当年无日志时为 null）
     */
    private String topMood;

    /**
     * 恋爱清单完成数（清单无年份维度，取当前累计值）
     */
    private Integer loveListDone;

    /**
     * 恋爱清单总条数（取当前累计值）
     */
    private Integer loveListTotal;

    /**
     * 当年访问量合计（PV）
     */
    private Long pv;

    /**
     * 当年独立访客数合计（UV）
     */
    private Long uv;

    /**
     * 当年精选回忆（画册照片/点点滴滴/情侣日志混排，最多 6 条）
     */
    private List<TimelineItemResponse> highlights;

}
