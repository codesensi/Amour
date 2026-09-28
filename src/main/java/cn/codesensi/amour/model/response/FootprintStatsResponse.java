package cn.codesensi.amour.model.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 足迹年度统计响应结果。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class FootprintStatsResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 统计年份
     */
    private Integer year;

    /**
     * 当年到访次数
     */
    private Integer totalVisits;

    /**
     * 当年到访城市数（去重）
     */
    private Integer totalCities;

    /**
     * 月度到访次数（1-12 月逐月补齐，无到访的月份计 0）
     */
    private List<MonthCount> byMonth;

    /**
     * 城市到访次数排行（降序）
     */
    private List<CityCount> topCities;

    /**
     * 月度计数条目
     */
    @Data
    public static class MonthCount implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 月份（1-12）
         */
        private Integer month;

        /**
         * 到访次数
         */
        private Integer count;
    }

    /**
     * 城市计数条目
     */
    @Data
    public static class CityCount implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 城市名称
         */
        private String city;

        /**
         * 到访次数
         */
        private Integer count;
    }

}
