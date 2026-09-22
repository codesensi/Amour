package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.MessageAuditDTO;
import cn.codesensi.amour.model.dto.MessageDTO;
import cn.codesensi.amour.model.dto.MessagePageDTO;
import cn.codesensi.amour.model.dto.MessageSubmitDTO;
import cn.codesensi.amour.model.entity.PortalMessage;
import cn.codesensi.amour.model.request.MessageAuditRequest;
import cn.codesensi.amour.model.request.MessagePageRequest;
import cn.codesensi.amour.model.request.MessageSubmitRequest;
import cn.codesensi.amour.model.response.MessagePageResponse;
import cn.codesensi.amour.model.response.PortalMessageResponse;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * 留言转换器（门户与管理端共用，MapStruct 编译期生成实现类）。
 * <p>
 * 门户响应字段对齐前端契约，由 {@code createTime→date}、{@code region→location}
 * 的重命名映射完成；管理端字段与表列同名，均为自动映射。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MessageConverter {

    /**
     * 提交留言请求 → 提交留言参数 DTO。
     *
     * @param request 提交留言请求
     * @return 提交留言参数 DTO
     */
    MessageSubmitDTO toSubmitDTO(MessageSubmitRequest request);

    /**
     * 留言分页查询请求 → 留言分页查询参数 DTO。
     *
     * @param request 留言分页查询请求
     * @return 留言分页查询参数 DTO
     */
    MessagePageDTO toPageDTO(MessagePageRequest request);

    /**
     * 留言审核请求 → 留言审核参数 DTO。
     *
     * @param request 留言审核请求
     * @return 留言审核参数 DTO
     */
    MessageAuditDTO toAuditDTO(MessageAuditRequest request);

    /**
     * 提交留言参数 DTO → 实体（头像快照/IP/归属地/审核状态由 Service 显式赋值后写入；
     * 审计字段由基类监听器与全局逻辑删除配置承接）。
     *
     * @param submitDTO 提交留言参数 DTO
     * @return 留言实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nickname", source = "name")
    @Mapping(target = "content", source = "text")
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "ip", ignore = true)
    @Mapping(target = "region", ignore = true)
    @Mapping(target = "auditStatus", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    PortalMessage toEntity(MessageSubmitDTO submitDTO);

    /**
     * 实体 → Service 出参 DTO（字段同名自动映射）。
     *
     * @param entity 留言实体
     * @return 留言条目 DTO
     */
    MessageDTO toDTO(PortalMessage entity);

    /**
     * Page&lt;PortalMessage&gt; → Page&lt;MessageDTO&gt;
     * （records 逐元素复用实体 → 条目 DTO 的映射规则）。
     *
     * @param page 留言实体分页
     * @return 留言条目 DTO 分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<MessageDTO> toPageDTO(Page<PortalMessage> page);

    /**
     * 条目 DTO → 门户留言响应。
     * <p>
     * {@code createTime→date} 按门户契约格式化为定长字符串，{@code region→location} 对齐前端字段名。
     *
     * @param itemDTO 留言条目 DTO
     * @return 门户留言响应对象
     */
    @Mapping(source = "createTime", target = "date", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "region", target = "location")
    PortalMessageResponse toPortalResponse(MessageDTO itemDTO);

    /**
     * Page&lt;MessageDTO&gt; → Page&lt;PortalMessageResponse&gt;
     * （records 逐元素复用 {@link #toPortalResponse(MessageDTO)} 的映射规则）。
     *
     * @param page 留言条目 DTO 分页
     * @return 门户留言响应分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<PortalMessageResponse> toPortalPage(Page<MessageDTO> page);

    /**
     * 条目 DTO → 管理端分页行响应（createTime 按 yyyy-MM-dd HH:mm:ss 格式化）。
     *
     * @param itemDTO 留言条目 DTO
     * @return 管理端行响应对象
     */
    @Mapping(source = "createTime", target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    MessagePageResponse toResponse(MessageDTO itemDTO);

    /**
     * Page&lt;MessageDTO&gt; → Page&lt;MessagePageResponse&gt;
     * （records 逐元素复用 {@link #toResponse(MessageDTO)} 的映射规则）。
     *
     * @param page 留言条目 DTO 分页
     * @return 管理端行响应对象分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<MessagePageResponse> toPageResponse(Page<MessageDTO> page);

}
