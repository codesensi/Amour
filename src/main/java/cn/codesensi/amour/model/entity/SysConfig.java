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
 * 系统配置实体。
 * <p>
 * 存储 {@code app.*} 业务可调配置，运行期由 {@code ConfigService} 实时查库读取（热更新）。
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table("sys_config")
public class SysConfig extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 配置键（app 之下的点分路径，如 captcha.sms-expire）
     */
    private String cKey;

    /**
     * 配置值（统一字符串存储）
     */
    private String cValue;

    /**
     * 值类型:STRING,INTEGER,LONG,BOOLEAN
     */
    private String vType;

    /**
     * 分组（app 的一级子项，如 name、captcha 等）
     */
    private String cGroup;

    /**
     * 配置状态:0-启用,1-禁用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}
