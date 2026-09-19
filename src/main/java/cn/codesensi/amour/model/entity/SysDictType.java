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
 * 数据字典类型实体。
 * <p>
 * 一个字典编码一行，承载类型名与类型级元数据（备注等）；字典条目见 {@link SysDictData}。
 * 内置类型（builtin=1）为系统功能依赖，禁删且编码不可改，名称/备注可维护。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("sys_dict_type")
public class SysDictType extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 字典编码（kebab-case，如 gender、menu-type）
     */
    private String dictCode;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 是否内置:0-否，1-是（内置类型禁删、编码不可改）
     */
    private Integer builtin;

    /**
     * 备注
     */
    private String remark;

}
