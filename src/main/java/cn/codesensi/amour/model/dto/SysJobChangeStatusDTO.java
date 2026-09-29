package cn.codesensi.amour.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务启停 DTO。
 *
 * @author codesensi
 * @since 1.0
 */
@Data
public class SysJobChangeStatusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private String id;

    /**
     * 目标状态: 0-正常(恢复)， 1-暂停
     */
    private Integer status;

}
