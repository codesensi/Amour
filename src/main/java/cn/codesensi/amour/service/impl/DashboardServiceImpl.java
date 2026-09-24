package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.enums.HiddenEnum;
import cn.codesensi.amour.mapper.*;
import cn.codesensi.amour.model.dto.DashboardSummaryDTO;
import cn.codesensi.amour.model.dto.DashboardTimelineItemDTO;
import cn.codesensi.amour.model.entity.PortalAnniversary;
import cn.codesensi.amour.model.entity.PortalMessage;
import cn.codesensi.amour.service.AnniversaryService;
import cn.codesensi.amour.service.DashboardService;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static cn.codesensi.amour.model.entity.table.PortalAnniversaryTableDef.PORTAL_ANNIVERSARY;
import static cn.codesensi.amour.model.entity.table.PortalDiaryTableDef.PORTAL_DIARY;
import static cn.codesensi.amour.model.entity.table.PortalFootprintTableDef.PORTAL_FOOTPRINT;
import static cn.codesensi.amour.model.entity.table.PortalLoveListTableDef.PORTAL_LOVE_LIST;
import static cn.codesensi.amour.model.entity.table.PortalLovePhotoTableDef.PORTAL_LOVE_PHOTO;
import static cn.codesensi.amour.model.entity.table.PortalMessageTableDef.PORTAL_MESSAGE;
import static cn.codesensi.amour.model.entity.table.PortalMomentsTableDef.PORTAL_MOMENTS;

/**
 * 管理端首页数据聚合 Service 实现。
 * <p>
 * 全部取数均走 MyBatis-Flex 实体 Mapper 强类型查询,逻辑删除由
 * {@code logic-delete-column} 全局配置自动过滤,原生 SQL 已全部迁移清除。
 * <p>
 * 时间线为画册照片/点点滴滴/情侣日志三类来源的混排:各自取「页码 × 每页条数」条,
 * Java 合并排序后内存分页。
 *
 * @author codesensi
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final PortalAnniversaryMapper portalAnniversaryMapper;
    private final PortalLovePhotoMapper portalLovePhotoMapper;
    private final PortalFootprintMapper portalFootprintMapper;
    private final PortalMessageMapper portalMessageMapper;
    private final PortalMomentsMapper portalMomentsMapper;
    private final PortalDiaryMapper portalDiaryMapper;
    private final PortalTimeCapsuleMapper portalTimeCapsuleMapper;
    private final PortalLoveListMapper portalLoveListMapper;
    private final AnniversaryService anniversaryService;

    /**
     * 时间线摘要截断长度（字符数）
     */
    private static final int EXCERPT_LENGTH = 200;

    /**
     * 最近回忆时间线固定展示条数
     */
    private static final int TIMELINE_LIMIT = 6;

    /**
     * {@inheritDoc}
     * <p>
     * 各查询相互独立、均可为空：某模块尚无数据时对应字段为 null 或 0，由前端空状态渲染。
     */
    @Override
    public DashboardSummaryDTO summary() {
        DashboardSummaryDTO summary = new DashboardSummaryDTO();

        // 在一起天数：最早一条每年重复纪念日的间隔天数（当天计 1 天）
        PortalAnniversary earliest = QueryChain.of(portalAnniversaryMapper)
                .select(PORTAL_ANNIVERSARY.ANNIVERSARY_DATE)
                .where(PORTAL_ANNIVERSARY.REPEAT_YEARLY.eq(1))
                .orderBy(PORTAL_ANNIVERSARY.ANNIVERSARY_DATE, true)
                .limit(1)
                .one();
        if (ObjUtil.isNotNull(earliest)) {
            long days = ChronoUnit.DAYS.between(earliest.getAnniversaryDate(), LocalDate.now()) + 1;
            summary.setTogetherDays((int) days);
        }

        // 下一个纪念日（复用门户口径）
        summary.setNextAnniversary(anniversaryService.next());

        // 各模块条目计数
        DashboardSummaryDTO.ModuleCounts counts = new DashboardSummaryDTO.ModuleCounts();
        counts.setAnniversaries(Math.toIntExact(QueryChain.of(portalAnniversaryMapper).count()));
        counts.setMoments(Math.toIntExact(QueryChain.of(portalMomentsMapper).count()));
        counts.setPhotos(Math.toIntExact(QueryChain.of(portalLovePhotoMapper).count()));
        counts.setMessages(Math.toIntExact(QueryChain.of(portalMessageMapper).count()));
        counts.setDiaries(Math.toIntExact(QueryChain.of(portalDiaryMapper).count()));
        counts.setTimeCapsules(Math.toIntExact(QueryChain.of(portalTimeCapsuleMapper).count()));
        counts.setFootprints(Math.toIntExact(QueryChain.of(portalFootprintMapper).count()));
        summary.setCounts(counts);

        // 恋爱清单进度（done:0-待完成,1-已完成）
        DashboardSummaryDTO.LoveListProgress progress = new DashboardSummaryDTO.LoveListProgress();
        progress.setTotal(Math.toIntExact(QueryChain.of(portalLoveListMapper).count()));
        progress.setDone(Math.toIntExact(QueryChain.of(portalLoveListMapper)
                .where(PORTAL_LOVE_LIST.DONE.eq(1))
                .count()));
        summary.setLoveListProgress(progress);

        // 足迹去重城市数（仅显示状态）
        List<String> cities = QueryChain.of(portalFootprintMapper)
                .select(PORTAL_FOOTPRINT.CITY)
                .where(PORTAL_FOOTPRINT.HIDDEN.eq(HiddenEnum.SHOW.getCode()))
                .listAs(String.class);
        summary.setFootprintsCityCount((int) cities.stream().distinct().count());

        // 恋爱画册最近照片（照片墙用，最多 9 张）
        List<String> recentPhotos = QueryChain.of(portalLovePhotoMapper)
                .select(PORTAL_LOVE_PHOTO.URL)
                .orderBy(PORTAL_LOVE_PHOTO.CREATE_TIME, false)
                .limit(9)
                .listAs(String.class);
        if (!recentPhotos.isEmpty()) {
            summary.setRecentPhotos(recentPhotos);
        }

        // 最新一条留言（口径保持「未删除全部」：管理端首页需感知待审核留言）
        PortalMessage latest = QueryChain.of(portalMessageMapper)
                .select(PORTAL_MESSAGE.NICKNAME, PORTAL_MESSAGE.CONTENT, PORTAL_MESSAGE.CREATE_TIME)
                .orderBy(PORTAL_MESSAGE.CREATE_TIME, false)
                .limit(1)
                .one();
        if (ObjUtil.isNotNull(latest)) {
            DashboardSummaryDTO.LatestMessage latestMessage = new DashboardSummaryDTO.LatestMessage();
            latestMessage.setNickname(latest.getNickname());
            latestMessage.setContent(latest.getContent());
            latestMessage.setCreateTime(LocalDateTimeUtil.format(latest.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN));
            summary.setLatestMessage(latestMessage);
        }

        return summary;
    }

    /**
     * 最近回忆时间线：三类来源各取样 {@value TIMELINE_LIMIT} 条,Java 合并按时间倒序后
     * 截取前 {@value TIMELINE_LIMIT} 条 —— 结果即「全部内容整合后的最新
     * {@value TIMELINE_LIMIT} 条」。
     * <p>
     * 取样条数与展示条数相等即可保证不漏:任一来源在全局最新 {@value TIMELINE_LIMIT}
     * 条中至多占 {@value TIMELINE_LIMIT} 条,故各来源自身最新的 {@value TIMELINE_LIMIT}
     * 条必然完整覆盖其在全局前 {@value TIMELINE_LIMIT} 中的条目,无需取全量再排序。
     * <p>
     * 混排时间统一取各表 {@code create_time}（记录业务日期可能为空，不参与排序），
     * 格式化为定长字符串后字典序即时间序。
     */
    @Override
    public Page<DashboardTimelineItemDTO> timeline() {
        List<DashboardTimelineItemDTO> all = new ArrayList<>();
        all.addAll(queryPhotos(TIMELINE_LIMIT));
        all.addAll(queryMoments(TIMELINE_LIMIT));
        all.addAll(queryDiary(TIMELINE_LIMIT));
        all.sort((a, b) -> b.getTime().compareTo(a.getTime()));
        List<DashboardTimelineItemDTO> records = all.subList(0, Math.min(TIMELINE_LIMIT, all.size())
        );
        return new Page<>(records, 1, TIMELINE_LIMIT, records.size());
    }

    /**
     * 查询恋爱画册照片条目。
     *
     * @param limit 取样条数
     * @return 照片类时间线条目
     */
    private List<DashboardTimelineItemDTO> queryPhotos(long limit) {
        return QueryChain.of(portalLovePhotoMapper)
                .select(PORTAL_LOVE_PHOTO.CAPTION, PORTAL_LOVE_PHOTO.URL, PORTAL_LOVE_PHOTO.CREATE_TIME)
                .orderBy(PORTAL_LOVE_PHOTO.CREATE_TIME, false)
                .limit(limit)
                .list()
                .stream()
                .map(photo -> {
                    DashboardTimelineItemDTO item = new DashboardTimelineItemDTO();
                    item.setType(DashboardTimelineItemDTO.TimelineItemType.photos);
                    item.setTime(LocalDateTimeUtil.format(photo.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN));
                    item.setTitle(StrUtil.blankToDefault(photo.getCaption(), "照片"));
                    item.setImageUrl(photo.getUrl());
                    return item;
                })
                .toList();
    }

    /**
     * 查询点点滴滴文章条目。
     *
     * @param limit 取样条数
     * @return 文章类时间线条目
     */
    private List<DashboardTimelineItemDTO> queryMoments(long limit) {
        return QueryChain.of(portalMomentsMapper)
                .select(PORTAL_MOMENTS.TITLE, PORTAL_MOMENTS.CONTENT, PORTAL_MOMENTS.CREATE_TIME)
                .orderBy(PORTAL_MOMENTS.CREATE_TIME, false)
                .limit(limit)
                .list()
                .stream()
                .map(moment -> {
                    DashboardTimelineItemDTO item = new DashboardTimelineItemDTO();
                    item.setType(DashboardTimelineItemDTO.TimelineItemType.moments);
                    item.setTime(LocalDateTimeUtil.format(moment.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN));
                    item.setTitle(moment.getTitle());
                    item.setContent(excerpt(moment.getContent()));
                    return item;
                })
                .toList();
    }

    /**
     * 查询情侣日志条目。
     *
     * @param limit 取样条数
     * @return 日志类时间线条目
     */
    private List<DashboardTimelineItemDTO> queryDiary(long limit) {
        return QueryChain.of(portalDiaryMapper)
                .select(PORTAL_DIARY.CONTENT, PORTAL_DIARY.CREATE_TIME)
                .orderBy(PORTAL_DIARY.CREATE_TIME, false)
                .limit(limit)
                .list()
                .stream()
                .map(diary -> {
                    DashboardTimelineItemDTO item = new DashboardTimelineItemDTO();
                    item.setType(DashboardTimelineItemDTO.TimelineItemType.diary);
                    item.setTime(LocalDateTimeUtil.format(diary.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN));
                    item.setTitle("情侣日志");
                    item.setContent(excerpt(diary.getContent()));
                    return item;
                })
                .toList();
    }

    /**
     * 摘要截断:取正文前 {@value EXCERPT_LENGTH} 个字符（与原 SUBSTRING 口径一致）。
     *
     * @param content 正文 HTML
     * @return 截断后的摘要;空正文返回 null
     */
    private String excerpt(String content) {
        if (StrUtil.isBlank(content)) {
            return null;
        }
        return content.length() > EXCERPT_LENGTH ? StrUtil.subPre(content, EXCERPT_LENGTH) : content;
    }

}
