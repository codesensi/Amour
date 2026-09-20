package cn.codesensi.amour.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 文件类型枚举 —— 项目级基础文件类型，供上传校验、类型识别等场景统一使用。
 * <p>
 * 承载每种类型的接受扩展名（含 jpeg/jpg 这类别名）与权威 MIME 类型；
 * 魔数判定由本枚举自行实现：{@link #match} 按各格式公开的文件头签名做前缀比对，
 * 识别入口 {@link #fromMagic(byte[])} 遍历枚举项取首个命中项，全部未命中返回 {@code null}。
 * 动图 PNG（APNG）的文件头与本枚举 PNG 签名一致，天然归一到 {@link #PNG}。
 * <p>
 * 纯文本类（txt/md）无固定魔数，不在本枚举收录范围。
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum FileTypeEnum implements BaseEnum<String> {

    /**
     * JPEG 图片:固定起始 FF D8 FF
     */
    JPG("jpg", "JPEG 图片", "image/jpeg", new String[]{"jpg", "jpeg"}) {
        @Override
        public boolean match(byte[] head) {
            return head.length >= 3
                    && head[0] == (byte) 0xFF && head[1] == (byte) 0xD8 && head[2] == (byte) 0xFF;
        }
    },

    /**
     * PNG 图片:固定起始 89 50 4E 47 0D 0A 1A 0A
     */
    PNG("png", "PNG 图片", "image/png", new String[]{"png"}) {
        @Override
        public boolean match(byte[] head) {
            return head.length >= 8
                    && head[0] == (byte) 0x89 && head[1] == 0x50 && head[2] == 0x4E && head[3] == 0x47
                    && head[4] == 0x0D && head[5] == 0x0A && head[6] == 0x1A && head[7] == 0x0A;
        }
    },

    /**
     * GIF 图片:GIF87a/GIF89a 共同前缀 GIF8
     */
    GIF("gif", "GIF 图片", "image/gif", new String[]{"gif"}) {
        @Override
        public boolean match(byte[] head) {
            return head.length >= 4
                    && head[0] == 0x47 && head[1] == 0x49 && head[2] == 0x46 && head[3] == 0x38;
        }
    },

    /**
     * WebP 图片:RIFF 容器标记(偏移 0)与 WEBP 特征串(偏移 8)两段组合判定
     */
    WEBP("webp", "WebP 图片", "image/webp", new String[]{"webp"}) {
        @Override
        public boolean match(byte[] head) {
            return head.length >= 12
                    && head[0] == 0x52 && head[1] == 0x49 && head[2] == 0x46 && head[3] == 0x46
                    && head[8] == 0x57 && head[9] == 0x45 && head[10] == 0x42 && head[11] == 0x50;
        }
    },

    /**
     * BMP 图片:固定起始 42 4D
     */
    BMP("bmp", "BMP 图片", "image/bmp", new String[]{"bmp"}) {
        @Override
        public boolean match(byte[] head) {
            return head.length >= 2 && head[0] == 0x42 && head[1] == 0x4D;
        }
    },

    /**
     * ICO 图标:固定起始 00 00 01 00
     */
    ICO("ico", "ICO 图标", "image/x-icon", new String[]{"ico"}) {
        @Override
        public boolean match(byte[] head) {
            return head.length >= 4
                    && head[0] == 0x00 && head[1] == 0x00 && head[2] == 0x01 && head[3] == 0x00;
        }
    },

    /**
     * PDF 文档:固定起始 %PDF(可带 UTF-8 BOM 前缀)
     */
    PDF("pdf", "PDF 文档", "application/pdf", new String[]{"pdf"}) {
        @Override
        public boolean match(byte[] head) {
            int offset = head.length >= 3
                    && head[0] == (byte) 0xEF && head[1] == (byte) 0xBB && head[2] == (byte) 0xBF ? 3 : 0;
            return head.length >= offset + 4
                    && head[offset] == 0x25 && head[offset + 1] == 0x50
                    && head[offset + 2] == 0x44 && head[offset + 3] == 0x46;
        }
    },

    /**
     * ZIP 压缩包:固定起始 PK+版本字节(50 4B 03 04 常规,05/06、07/08 为稀疏分卷)
     */
    ZIP("zip", "ZIP 压缩包", "application/zip", new String[]{"zip"}) {
        @Override
        public boolean match(byte[] head) {
            if (head.length < 4 || head[0] != 0x50 || head[1] != 0x4B) {
                return false;
            }
            boolean second = head[2] == 0x03 || head[2] == 0x05 || head[2] == 0x07;
            boolean third = head[3] == 0x04 || head[3] == 0x06 || head[3] == 0x08;
            return second && third;
        }
    },

    /**
     * RAR 压缩包:固定起始 Rar!(52 61 72 21 1A 07)
     */
    RAR("rar", "RAR 压缩包", "application/vnd.rar", new String[]{"rar"}) {
        @Override
        public boolean match(byte[] head) {
            return head.length >= 6
                    && head[0] == 0x52 && head[1] == 0x61 && head[2] == 0x72
                    && head[3] == 0x21 && head[4] == 0x1A && head[5] == 0x07;
        }
    },

    /**
     * 7Z 压缩包:固定起始 37 7A BC AF 27 1C
     */
    SEVEN_Z("7z", "7Z 压缩包", "application/x-7z-compressed", new String[]{"7z"}) {
        @Override
        public boolean match(byte[] head) {
            return head.length >= 6
                    && head[0] == 0x37 && head[1] == 0x7A && head[2] == (byte) 0xBC
                    && head[3] == (byte) 0xAF && head[4] == 0x27 && head[5] == 0x1C;
        }
    },

    /**
     * GZ 压缩包:固定起始 1F 8B 08
     */
    GZ("gz", "GZ 压缩包", "application/gzip", new String[]{"gz", "gzip"}) {
        @Override
        public boolean match(byte[] head) {
            return head.length >= 3
                    && head[0] == 0x1F && head[1] == (byte) 0x8B && head[2] == 0x08;
        }
    },

    /**
     * MP3 音频:ID3 标签头(49 44 33)或 MPEG 帧头(FF FB/FF F3/FF F2)
     */
    MP3("mp3", "MP3 音频", "audio/mpeg", new String[]{"mp3"}) {
        @Override
        public boolean match(byte[] head) {
            if (head.length < 2) {
                return false;
            }
            boolean id3 = head[0] == 0x49 && head[1] == 0x44 && head.length >= 3 && head[2] == 0x33;
            boolean frame = head[0] == (byte) 0xFF
                    && (head[1] == (byte) 0xFB || head[1] == (byte) 0xF3 || head[1] == (byte) 0xF2);
            return id3 || frame;
        }
    },

    /**
     * MP4 视频:偏移 4 起为 ftyp 盒类型
     */
    MP4("mp4", "MP4 视频", "video/mp4", new String[]{"mp4"}) {
        @Override
        public boolean match(byte[] head) {
            return head.length >= 8
                    && head[4] == 0x66 && head[5] == 0x74 && head[6] == 0x79 && head[7] == 0x70;
        }
    };

    /**
     * 主扩展名（全小写，作为编码）
     */
    private final String code;

    /**
     * 类型说明
     */
    private final String desc;

    /**
     * 权威 MIME 类型
     */
    private final String mimeType;

    /**
     * 接受的上传扩展名（全小写，含 jpg/jpeg 这类别名）
     */
    private final String[] aliases;

    /**
     * 判断文件头是否命中本类型的魔数签名。
     *
     * @param head 文件头字节
     * @return true 表示命中
     */
    public abstract boolean match(byte[] head);

    /**
     * 按上传扩展名定位类型（含别名反查）。
     *
     * @param extension 全小写扩展名
     * @return 对应类型；未收录返回 {@code null}
     */
    public static FileTypeEnum fromExtension(String extension) {
        return Arrays.stream(values())
                .filter(type -> Arrays.asList(type.aliases).contains(extension))
                .findFirst()
                .orElse(null);
    }

    /**
     * 按文件头魔数识别类型，遍历枚举项取首个命中项。
     *
     * @param head 文件头字节
     * @return 识别到的类型；全部未命中返回 {@code null}
     */
    public static FileTypeEnum fromMagic(byte[] head) {
        for (FileTypeEnum type : values()) {
            if (type.match(head)) {
                return type;
            }
        }
        return null;
    }
}
