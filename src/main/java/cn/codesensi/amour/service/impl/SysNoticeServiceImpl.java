package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.mapper.SysNoticeMapper;
import cn.codesensi.amour.mapper.SysNoticeReadMapper;
import cn.codesensi.amour.model.converter.NoticeConverter;
import cn.codesensi.amour.model.dto.NoticeDTO;
import cn.codesensi.amour.model.entity.SysNotice;
import cn.codesensi.amour.model.entity.SysNoticeRead;
import cn.codesensi.amour.service.SysNoticeService;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.codesensi.amour.model.entity.table.SysNoticeReadTableDef.SYS_NOTICE_READ;
import static cn.codesensi.amour.model.entity.table.SysNoticeTableDef.SYS_NOTICE;

/**
 * 通知 服务层实现。
 * <p>
 * 列表与已读判定采用两次单表查询（通知 + 当前用户已读记录），在内存中组装
 * read 标记，避免关联查询；标记已读按 (user_id, notice_id) 唯一键幂等写入。
 * <p>
 * 逻辑删除（del_flag）由 MyBatis-Flex 全局配置自动追加过滤，查询无需显式条件。
 *
 * @author codesensi
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl implements SysNoticeService {

    private final SysNoticeMapper sysNoticeMapper;

    private final SysNoticeReadMapper sysNoticeReadMapper;

    private final NoticeConverter noticeConverter;

    /**
     * 查询当前用户的通知列表（含当前用户的已读标记）。
     * <p>
     * 两次单表查询：通知列表 + 当前用户对这批通知的已读ID集合，内存组装 read 标记。
     *
     * @param limit 最大返回条数
     * @return 通知响应列表；无通知时返回空列表
     */
    @Override
    public List<NoticeDTO> list(Integer limit) {
        List<SysNotice> notices = QueryChain.of(sysNoticeMapper)
                .orderBy(SYS_NOTICE.CREATE_TIME, false)
                .limit(limit)
                .list();
        if (CollUtil.isEmpty(notices)) {
            return List.of();
        }
        Long userId = StpUtil.getLoginIdAsLong();
        List<Long> noticeIds = notices.stream().map(SysNotice::getId).toList();
        Set<Long> readNoticeIds = new HashSet<>(QueryChain.of(sysNoticeReadMapper)
                .select(SYS_NOTICE_READ.NOTICE_ID)
                .where(SYS_NOTICE_READ.USER_ID.eq(userId))
                .and(SYS_NOTICE_READ.NOTICE_ID.in(noticeIds))
                .listAs(Long.class));
        List<NoticeDTO> noticeDTOs = noticeConverter.toListDTO(notices);
        noticeDTOs.forEach(noticeDTO -> noticeDTO.setRead(readNoticeIds.contains(noticeDTO.getId())));
        return noticeDTOs;
    }

    /**
     * 标记通知已读（幂等）。
     * <p>
     * 仅写入当前用户尚无已读记录的通知：先查已存在的记录，再批量插入缺失部分，
     * 唯一键 (user_id, notice_id) 兜底保证并发下不产生重复行。
     *
     * @param noticeIds 通知ID集合（可空，空=全部未读）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void read(List<Long> noticeIds) {
        // 解析待标记的通知ID：仅接受真实存在的通知，防御伪造ID
        List<Long> targetIds = QueryChain.of(sysNoticeMapper)
                .select(SYS_NOTICE.ID)
                .where(SYS_NOTICE.ID.in(noticeIds, CollUtil::isNotEmpty))
                .listAs(Long.class);
        if (CollUtil.isEmpty(targetIds)) {
            return;
        }

        // 剔除已读部分，只补插缺失的已读记录
        Long userId = StpUtil.getLoginIdAsLong();
        Set<Long> readNoticeIds = new HashSet<>(QueryChain.of(sysNoticeReadMapper)
                .select(SYS_NOTICE_READ.NOTICE_ID)
                .where(SYS_NOTICE_READ.USER_ID.eq(userId))
                .and(SYS_NOTICE_READ.NOTICE_ID.in(targetIds))
                .listAs(Long.class));
        List<SysNoticeRead> missingReads = targetIds.stream()
                .filter(noticeId -> !readNoticeIds.contains(noticeId))
                .map(noticeId -> new SysNoticeRead().setUserId(userId).setNoticeId(noticeId))
                .toList();
        if (CollUtil.isEmpty(missingReads)) {
            return;
        }
        sysNoticeReadMapper.insertBatch(missingReads);
    }

}
