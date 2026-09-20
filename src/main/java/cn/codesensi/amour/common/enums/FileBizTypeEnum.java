package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 文件业务类型枚举 —— 每种业务类型自带文件格式白名单与单文件大小上限，
 * 上传接口按路径参数中的 bizType 路由并以此校验，新增业务类型仅需补充枚举项。
 * <p>
 * 说明：biz_type 是代码级路由与校验键，校验规则承载于本枚举，
 * 暂不维护进 sys_dict（待文件管理页需要标签展示时再补 file-biz-type 字典）。
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum FileBizTypeEnum implements BaseEnum<String> {

    INFRA("infra", "基础设施", List.of(FileTypeEnum.JPG, FileTypeEnum.PNG, FileTypeEnum.GIF, FileTypeEnum.WEBP, FileTypeEnum.ICO), 2),
    AVATAR("avatar", "用户头像", List.of(FileTypeEnum.JPG, FileTypeEnum.PNG, FileTypeEnum.GIF, FileTypeEnum.WEBP), 2),
    PHOTO("photo", "相册照片", List.of(FileTypeEnum.JPG, FileTypeEnum.PNG, FileTypeEnum.WEBP), 20),
    MARKDOWN("markdown", "点滴配图", List.of(FileTypeEnum.JPG, FileTypeEnum.PNG, FileTypeEnum.GIF, FileTypeEnum.WEBP), 10),
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
     * 允许的文件格式白名单（jpeg/jpg 这类别名由枚举项的 aliases 承载）
     */
    private final List<FileTypeEnum> formats;

    /**
     * 单文件大小上限（MB）
     */
    private final int maxMb;

    /**
     * 判断上传扩展名是否在格式白名单内（按枚举别名匹配，如 jpeg 命中 JPG 项）。
     *
     * @param extension 全小写扩展名
     * @return true 表示白名单内
     */
    public boolean accepts(String extension) {
        return extension != null && formats.contains(FileTypeEnum.fromExtension(extension));
    }
}
