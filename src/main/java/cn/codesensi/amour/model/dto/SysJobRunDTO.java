package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务手动执行 DTO。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class SysJobRunDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private String id;

}
