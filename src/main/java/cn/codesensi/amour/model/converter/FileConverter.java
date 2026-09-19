package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.FileInfoDTO;
import cn.codesensi.amour.model.dto.FilePageDTO;
import cn.codesensi.amour.model.dto.FileUploadResultDTO;
import cn.codesensi.amour.model.entity.SysFile;
import cn.codesensi.amour.model.request.FilePageRequest;
import cn.codesensi.amour.model.response.FilePageResponse;
import cn.codesensi.amour.model.response.FileUploadResponse;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * 文件转换器 —— {@link SysFile} 实体、DTO 与 Response 之间的映射（MapStruct 编译期生成实现类）。
 * <p>
 * 以 Spring Bean 方式注入使用（生成的 {@code FileConverterImpl} 为 Spring 组件）；
 * 同名字段（id/size/bizType 等）由 MapStruct 自动映射，上传人用户名由服务层批量回填。
 *
 * @author codesensi
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FileConverter {

    /**
     * 将分页查询请求转换为分页查询参数 DTO。
     *
     * @param request 分页查询请求
     * @return 分页查询参数 DTO
     */
    FilePageDTO toPageDTO(FilePageRequest request);

    /**
     * SysFile → FileInfoDTO 单条映射（Page 映射的 records 逐元素复用）；
     * creatorName 由服务层批量回填，实体无对应字段，显式忽略以消除 Unmapped 警告。
     *
     * @param file 文件实体
     * @return 文件行数据 DTO
     */
    @Mapping(target = "creatorName", ignore = true)
    FileInfoDTO toItemDTO(SysFile file);

    /**
     * Page&lt;SysFile&gt; → Page&lt;FileInfoDTO&gt;。
     *
     * @param page 文件实体分页
     * @return 文件行数据 DTO 分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<FileInfoDTO> toItemDTOPage(Page<SysFile> page);

    /**
     * FileInfoDTO → FilePageResponse 单条映射（Page 映射的 records 逐元素复用）。
     *
     * @param dto 文件行数据 DTO
     * @return 文件行响应对象
     */
    FilePageResponse toResponse(FileInfoDTO dto);

    /**
     * Page&lt;FileInfoDTO&gt; → Page&lt;FilePageResponse&gt;。
     *
     * @param dtoPage 文件行数据 DTO 分页
     * @return 文件行响应对象分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<FilePageResponse> toResponsePage(Page<FileInfoDTO> dtoPage);

    /**
     * FileUploadResultDTO → FileUploadResponse。
     *
     * @param dto 文件上传结果 DTO
     * @return 文件上传响应对象
     */
    FileUploadResponse toResponse(FileUploadResultDTO dto);

}
