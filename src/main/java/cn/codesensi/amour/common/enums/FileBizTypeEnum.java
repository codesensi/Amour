package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 文件业务类型枚举 —— 每种业务类型自带扩展名白名单与单文件大小上限，
 * 上传接口按路径参数中的 bizType 路由并以此校验，新增业务类型仅需补充枚举项。
 * <p>
 * 说明：biz_type 是代码级路由与校验键，校验规则承载于本枚举，
 * 暂不维护进 sys_dict（待文件管理页需要标签展示时再补 file-biz-type 字典）。
 *
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum FileBizTypeEnum implements BaseEnum<String> {

    AVATAR("avatar", "用户头像", List.of("jpg", "jpeg", "png", "gif", "webp"), 2),
    PHOTO("photo", "相册照片", List.of("jpg", "jpeg", "png", "webp"), 20),
    MARKDOWN("markdown", "点滴配图", List.of("jpg", "jpeg", "png", "gif", "webp"), 10),
    ;

    /**
     * 编码
     */
    private final String code;

    /**
     * 说明
     */
    private final String desc;

    /**
     * 扩展名白名单（全小写）
     */
    private final List<String> extensions;

    /**
     * 单文件大小上限（MB）
     */
    private final int maxMb;
}
