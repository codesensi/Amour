package cn.codesensi.amour.service;

import cn.codesensi.amour.common.enums.FileBizTypeEnum;
import cn.codesensi.amour.model.dto.FilePageDTO;
import cn.codesensi.amour.model.response.FilePageResponse;
import cn.codesensi.amour.model.response.FileUploadResponse;
import cn.codesensi.amour.service.file.FileViewResult;
import com.mybatisflex.core.paginate.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

/**
 * 文件服务 —— 上传、读取与业务采纳绑定。
 * <p>
 * 存储实现按 sys_config 的 {@code file.storage} 运行时路由（本地/对象存储），
 * 文件记录统一落 {@code sys_file} 表管理。
 *
 * @since 1.0
 */
public interface FileService {

    /**
     * 上传文件。
     * <p>
     * 按业务类型完成扩展名与大小校验后，先落库取得主键，再以主键命名写盘，
     * 任一环节失败整体回滚；返回的分发地址形如 {@code /file/view/{id}}。
     *
     * @param bizType 业务类型编码（avatar/photo/markdown）
     * @param file    上传的文件
     * @return 文件ID、访问地址与原始文件名
     */
    FileUploadResponse upload(String bizType, MultipartFile file);

    /**
     * 加载文件记录与存储资源。
     * <p>
     * 按文件自身记录的 storage_type 分发，与当前配置的存储方式无关，
     * 保证切换存储后历史文件仍可读取。
     *
     * @param id 文件ID
     * @return 文件记录与存储资源
     */
    FileViewResult load(Long id);

    /**
     * 分页查询文件记录。
     * <p>
     * 仅查询未删除记录，条件缺省时自动忽略；按 ID 倒序（最新在前）；
     * 上传人用户名按本页出现的 creator 批量回填。
     *
     * @param pageDTO 分页查询参数
     * @return 文件分页结果
     */
    Page<FilePageResponse> page(FilePageDTO pageDTO);

    /**
     * 删除文件：按存储类型路由清理物理文件，并逻辑删除记录（保留审计）。
     * <p>
     * 已被业务采纳（{@code biz_id} 非空）的文件默认拒绝删除，
     * {@code force=true} 表示管理端已确认业务影响后的强制删除。
     *
     * @param id    文件ID
     * @param force 是否强制删除被业务引用的文件
     */
    void delete(Long id, boolean force);

    /**
     * 将文件集合绑定到业务对象（采纳语义）。
     * <p>
     * 调用方传入业务对象当前引用的<b>全量</b> URL 集合：
     * 可解析出文件ID的（本系统文件）回填 {@code biz_id}；
     * 同业务类型与业务ID下本次未出现的旧文件标记逻辑删除（替换语义）；
     * 外链等无法解析的 URL 静默跳过，方法幂等可重入。
     *
     * @param bizType 业务类型
     * @param bizId   业务对象ID（新增场景由调用方在业务落库后传入）
     * @param urls    业务对象当前引用的全部文件 URL
     */
    void bindBizFiles(FileBizTypeEnum bizType, Long bizId, Collection<String> urls);
}
