package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.NoticeDTO;
import cn.codesensi.amour.model.dto.NoticeReadDTO;
import cn.codesensi.amour.model.entity.SysNotice;
import cn.codesensi.amour.model.request.NoticeReadRequest;
import cn.codesensi.amour.model.response.NoticeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * 通知相关对象转换。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NoticeConverter {

    /**
     * SysNotice → NoticeDTO（read 由服务层按当前用户组装，不在转换器内映射）
     */
    @Mapping(target = "read", ignore = true)
    NoticeDTO toDTO(SysNotice notice);

    /**
     * 将通知实体列表转换为 DTO 列表（逐元素复用 {@link #toDTO(SysNotice)} 的映射规则）。
     */
    List<NoticeDTO> toListDTO(List<SysNotice> notices);

    /**
     * NoticeDTO → NoticeResponse
     */
    NoticeResponse toResponse(NoticeDTO noticeDTO);

    /**
     * 将通知 DTO 列表转换为响应对象列表（逐元素复用 {@link #toResponse(NoticeDTO)} 的映射规则）。
     */
    List<NoticeResponse> toListResponse(List<NoticeDTO> noticeList);

    /**
     * NoticeReadRequest → NoticeReadDTO
     */
    NoticeReadDTO toReadDTO(NoticeReadRequest request);

}
