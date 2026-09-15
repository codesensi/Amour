package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.NoticeDTO;

import java.util.List;

/**
 * 通知 服务层。
 * <p>
 * 通知中心的数据读写：通知由业务事件触发写入（新增事件调用 {@link #save}），
 * 管理端不提供人工发布入口；查询与已读标记均以当前登录用户为视角。
 *
 * @author codesensi
 * @since 1.0
 */
public interface SysNoticeService {

    /**
     * 查询当前用户的通知列表（按创建时间倒序，每条附带当前用户的已读标记）。
     *
     * @param limit 最大返回条数；空值或非正数时回退默认值
     * @return 通知 DTO 列表；无通知时返回空列表
     */
    List<NoticeDTO> list(Integer limit);

    /**
     * 标记通知已读（按唯一键幂等写入，重复标记不报错）。
     * <p>
     * {@code noticeIds} 为空或缺失时标记当前用户的全部未读通知；
     * 传入的ID仅在通知存在且未读时生效，已读与不存在的ID自动忽略。
     *
     * @param noticeIds 通知ID集合（可空，空=全部未读）
     */
    void read(List<Long> noticeIds);

}
