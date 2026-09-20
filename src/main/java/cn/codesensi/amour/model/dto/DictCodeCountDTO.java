package cn.codesensi.amour.model.dto;

import lombok.Data;

/**
 * 字典编码计数投影 —— 条目表按编码 GROUP BY 聚合的行结果。
 * <p>仅供 {@code SysDictTypeServiceImpl.listTypes()} 的分组计数查询投影使用，
 * 字段名与 SQL 别名（dict_code/count 经下划线转驼峰）对齐。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class DictCodeCountDTO {

    /**
     * 字典编码
     */
    private String dictCode;

    /**
     * 该编码下的条目数（含禁用条目）
     */
    private Long count;

}
