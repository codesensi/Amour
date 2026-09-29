package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.SysJob;
import cn.codesensi.amour.model.entity.SysJobLog;
import cn.codesensi.amour.model.request.*;
import cn.codesensi.amour.model.response.SysJobLogPageResponse;
import cn.codesensi.amour.model.response.SysJobPageResponse;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * 定时任务相关对象转换。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysJobConverter {

    /**
     * SysJob → SysJobDTO（lastStatus/lastDuration/lastStartTime 由服务层聚合最近一条执行日志填充）
     */
    @Mapping(target = "lastStatus", ignore = true)
    @Mapping(target = "lastDuration", ignore = true)
    @Mapping(target = "lastStartTime", ignore = true)
    SysJobDTO toDTO(SysJob job);

    /**
     * SysJobInsertRequest → SysJobInsertDTO（新增参数由请求层契约转入服务层契约）
     */
    SysJobInsertDTO toInsertDTO(SysJobInsertRequest request);

    /**
     * SysJobUpdateRequest → SysJobUpdateDTO（修改参数由请求层契约转入服务层契约）
     */
    SysJobUpdateDTO toUpdateDTO(SysJobUpdateRequest request);

    /**
     * SysJobChangeStatusRequest → SysJobChangeStatusDTO（启停参数由请求层契约转入服务层契约）
     */
    SysJobChangeStatusDTO toChangeStatusDTO(SysJobChangeStatusRequest request);

    /**
     * SysJobRunRequest → SysJobRunDTO（手动执行参数由请求层契约转入服务层契约）
     */
    SysJobRunDTO toRunDTO(SysJobRunRequest request);

    /**
     * SysJobPageRequest → SysJobPageDTO（分页查询参数由请求层契约转入服务层契约）
     */
    SysJobPageDTO toPageDTO(SysJobPageRequest request);

    /**
     * Page&lt;SysJob&gt; → Page&lt;SysJobDTO&gt;（逐元素复用 {@link #toDTO(SysJob)}）
     */
    Page<SysJobDTO> toPageDTO(Page<SysJob> page);

    /**
     * SysJobInsertDTO → SysJob（id/status/builtin/审计字段由服务层与框架赋值，不在转换器内映射）
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "builtin", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    SysJob toEntity(SysJobInsertDTO insertDTO);

    /**
     * SysJobDTO → SysJobPageResponse
     */
    SysJobPageResponse toPageResponse(SysJobDTO jobDTO);

    /**
     * Page&lt;SysJobDTO&gt; → Page&lt;SysJobPageResponse&gt;（逐元素复用 {@link #toPageResponse(SysJobDTO)}）
     */
    Page<SysJobPageResponse> toPageResponse(Page<SysJobDTO> page);

    /**
     * SysJobLog → SysJobLogDTO
     */
    SysJobLogDTO toLogDTO(SysJobLog jobLog);

    /**
     * SysJobLogPageRequest → SysJobLogPageDTO（jobId 由字符串化传输值还原为 Long）
     */
    SysJobLogPageDTO toLogPageDTO(SysJobLogPageRequest request);

    /**
     * Page&lt;SysJobLog&gt; → Page&lt;SysJobLogDTO&gt;（逐元素复用 {@link #toLogDTO(SysJobLog)}）
     */
    Page<SysJobLogDTO> toLogPageDTO(Page<SysJobLog> page);

    /**
     * SysJobLogDTO → SysJobLogPageResponse
     */
    SysJobLogPageResponse toLogResponse(SysJobLogDTO jobLogDTO);

    /**
     * Page&lt;SysJobLogDTO&gt; → Page&lt;SysJobLogPageResponse&gt;（逐元素复用 {@link #toLogResponse(SysJobLogDTO)}）
     */
    Page<SysJobLogPageResponse> toLogPageResponse(Page<SysJobLogDTO> page);

}
