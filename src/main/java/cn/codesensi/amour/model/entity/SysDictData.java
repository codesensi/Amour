package cn.codesensi.amour.model.entity;

import cn.codesensi.amour.common.core.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据字典数据实体。
 * <p>
 * 每行代表一个字典条目，归属 {@code dict_code} 指向的字典类型（见 {@link SysDictType}）；
 * {@code dict_value}/{@code dict_label} 为字典项的值与展示标签。内置条目（builtin=1）
 * 仅承载展示层（标签、排序、启停），对应编码的业务校验仍由枚举类负责。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("sys_dict_data")
public class SysDictData extends BaseEntity implements Serializable {

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
     * 状态:0-启用，1-禁用
     */
    private Integer status;

    /**
     * 是否内置:0-否，1-是（内置条目锁定字典值）
     */
    private Integer builtin;

    /**
     * 备注
     */
    private String remark;

    /**
     * 字典名称（类型名，查询时自 sys_dict_type 回填的展示字段，非本表列）
     */
    @Column(ignore = true)
    private String dictName;

}
