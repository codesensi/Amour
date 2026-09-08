package cn.codesensi.amour.model.entity;

import cn.codesensi.amour.common.core.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据字典实体。
 * <p>
 * 扁平单表结构：每行代表一个字典项，{@code dict_code} 为字典编码（一组字典项的标识，
 * 命名与 {@code common/enums} 下现有枚举类对齐），{@code dict_value}/{@code dict_label}
 * 为字典项的值与展示标签。内置字典（builtin=1）仅承载展示层（标签、排序、启停），
 * 对应编码的业务校验仍由枚举类负责。
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("sys_dict")
public class SysDict extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 字典编码（如 gender、enable）
     */
    private String dictCode;

    /**
     * 字典名称（编码对应的字典名，同一编码下各行相同）
     */
    private String dictName;

    /**
     * 字典值（统一字符串存储；数字型枚举由调用侧自行转换类型）
     */
    private String dictValue;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sort;

    /**
     * 状态:0-启用,1-禁用
     */
    private Integer status;

    /**
     * 是否内置:0-否,1-是
     */
    private Integer builtin;

    /**
     * 备注
     */
    private String remark;

}
