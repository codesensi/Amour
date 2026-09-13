package cn.codesensi.amour.model.converter;

import cn.codesensi.amour.model.dto.FilePageDTO;
import cn.codesensi.amour.model.entity.SysFile;
import cn.codesensi.amour.model.request.FilePageRequest;
import cn.codesensi.amour.model.response.FilePageResponse;
import com.mybatisflex.core.paginate.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * 文件转换器 —— {@link SysFile} 实体转 {@link FilePageResponse}（MapStruct 编译期生成实现类）。
 * <p>
 * 以 Spring Bean 方式注入使用（生成的 {@code FileConverterImpl} 为 Spring 组件）；
 * 同名字段（id/size/bizType 等）由 MapStruct 自动映射，上传人用户名由服务层批量回填。
 *
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
     * Page&lt;SysFile&gt; → Page&lt;FilePageResponse&gt;
     * （records 逐元素复用实体 → 行响应对象的映射规则）。
     *
     * @param page 文件实体分页
     * @return 文件行响应对象分页
     */
    @Mapping(target = "optimizeCountQuery", ignore = true)
    Page<FilePageResponse> toPageResponse(Page<SysFile> page);

}
