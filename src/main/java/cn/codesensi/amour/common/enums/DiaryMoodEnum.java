package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 日记心情枚举（情侣日记的心情标识，取自常见天气与自然意象）。
 *
 * unknown-不标记（默认）
 * sunny-晴天
 * cloudy-多云
 * overcast-阴天
 * rainy-雨天
 * drizzle-细雨
 * thunderstorm-雷阵雨
 * windy-起风
 * snowy-落雪
 * sleet-雨夹雪
 * hail-冰雹
 * starry-星夜
 * bloom-花开
 * moon-月色
 * rainbow-彩虹
 * fog-薄雾
 * leaf-落叶
 * sunset-日落
 * meteor-流星
 * aurora-极光
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum DiaryMoodEnum implements BaseEnum<String> {

    UNKNOWN("unknown", "不标记"),
    SUNNY("sunny", "晴天"),
    CLOUDY("cloudy", "多云"),
    OVERCAST("overcast", "阴天"),
    RAINY("rainy", "雨天"),
    DRIZZLE("drizzle", "细雨"),
    THUNDERSTORM("thunderstorm", "雷阵雨"),
    WINDY("windy", "起风"),
    SNOWY("snowy", "落雪"),
    SLEET("sleet", "雨夹雪"),
    HAIL("hail", "冰雹"),
    STARRY("starry", "星夜"),
    BLOOM("bloom", "花开"),
    MOON("moon", "月色"),
    RAINBOW("rainbow", "彩虹"),
    FOG("fog", "薄雾"),
    LEAF("leaf", "落叶"),
    SUNSET("sunset", "日落"),
    METEOR("meteor", "流星"),
    AURORA("aurora", "极光"),
    ;

    /**
     * 编码
     */
    private final String code;

    /**
     * 说明
     */
    private final String desc;

}
