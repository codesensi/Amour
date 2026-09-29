package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.enums.DiaryMoodEnum;
import cn.codesensi.amour.common.enums.HiddenEnum;
import cn.codesensi.amour.common.enums.MessageAuditStatusEnum;
import cn.codesensi.amour.mapper.*;
import cn.codesensi.amour.model.dto.AnnualReviewDTO;
import cn.codesensi.amour.model.dto.DashboardMessageRegionDTO;
import cn.codesensi.amour.model.dto.DashboardSummaryDTO;
import cn.codesensi.amour.model.dto.DashboardTimelineItemDTO;
import cn.codesensi.amour.model.dto.DashboardVisitTrendItemDTO;
import cn.codesensi.amour.model.entity.PortalAnniversary;
import cn.codesensi.amour.model.entity.PortalMessage;
import cn.codesensi.amour.model.entity.PortalVisit;
import cn.codesensi.amour.service.AnniversaryService;
import cn.codesensi.amour.service.DashboardService;
import cn.hutool.core.collection.CollUtil;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.codesensi.amour.model.entity.table.PortalAnniversaryTableDef.PORTAL_ANNIVERSARY;
import static cn.codesensi.amour.model.entity.table.PortalDiaryTableDef.PORTAL_DIARY;
import static cn.codesensi.amour.model.entity.table.PortalFootprintTableDef.PORTAL_FOOTPRINT;
import static cn.codesensi.amour.model.entity.table.PortalLoveListTableDef.PORTAL_LOVE_LIST;
import static cn.codesensi.amour.model.entity.table.PortalLovePhotoTableDef.PORTAL_LOVE_PHOTO;
import static cn.codesensi.amour.model.entity.table.PortalMessageTableDef.PORTAL_MESSAGE;
import static cn.codesensi.amour.model.entity.table.PortalMomentsTableDef.PORTAL_MOMENTS;
import static cn.codesensi.amour.model.entity.table.PortalVisitTableDef.PORTAL_VISIT;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

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
    private final PortalVisitMapper portalVisitMapper;
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
     * 访问趋势统计天数下限
     */
    private static final int VISIT_TREND_MIN_DAYS = 7;

    /**
     * 访问趋势统计天数上限
     */
    private static final int VISIT_TREND_MAX_DAYS = 365;

    /**
     * 留言地区分布条数上限
     */
    private static final int MESSAGE_REGION_LIMIT = 20;

    /**
     * 年度回顾精选回忆条数
     */
    private static final int ANNUAL_HIGHLIGHT_LIMIT = 6;

    /**
     * 留言地区分布「未知」归并文案
     */
    private static final String REGION_UNKNOWN = "未知";

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
        if (CollUtil.isNotEmpty(recentPhotos)) {
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

        // 待审核留言数（口径与最新留言一致：未删除全部）
        summary.setPendingMessages(Math.toIntExact(QueryChain.of(portalMessageMapper)
                .where(PORTAL_MESSAGE.AUDIT_STATUS.eq(MessageAuditStatusEnum.PENDING.getCode()))
                .count()));

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
        all.addAll(queryPhotos(TIMELINE_LIMIT, null, null));
        all.addAll(queryMoments(TIMELINE_LIMIT, null, null));
        all.addAll(queryDiary(TIMELINE_LIMIT, null, null));
        all.sort((a, b) -> b.getTime().compareTo(a.getTime()));
        List<DashboardTimelineItemDTO> records = all.subList(0, Math.min(TIMELINE_LIMIT, all.size())
        );
        return new Page<>(records, 1, TIMELINE_LIMIT, records.size());
    }

    /**
     * {@inheritDoc}
     * <p>
     * 日期区间逐日补齐：无访问记录的日期计 0，保证趋势图 X 轴连续。
     */
    @Override
    public List<DashboardVisitTrendItemDTO> visitTrend(int days) {
        int limited = Math.max(VISIT_TREND_MIN_DAYS, Math.min(VISIT_TREND_MAX_DAYS, days));
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(limited - 1L);
        Map<LocalDate, PortalVisit> byDate = QueryChain.of(portalVisitMapper)
                .where(PORTAL_VISIT.STAT_DATE.ge(startDate))
                .and(PORTAL_VISIT.STAT_DATE.le(endDate))
                .list()
                .stream()
                .collect(Collectors.toMap(PortalVisit::getStatDate, visit -> visit, (first, second) -> first));
        List<DashboardVisitTrendItemDTO> result = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DashboardVisitTrendItemDTO item = new DashboardVisitTrendItemDTO();
            item.setStatDate(LocalDateTimeUtil.format(date, DatePattern.NORM_DATE_PATTERN));
            PortalVisit visit = byDate.get(date);
            item.setPv(ObjUtil.defaultIfNull(visit == null ? null : visit.getPv(), 0L));
            item.setUv(ObjUtil.defaultIfNull(visit == null ? null : visit.getUv(), 0L));
            result.add(item);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 数据量级为私人站点留言，Java 内存分组聚合即可；条数越界自动收敛。
     */
    @Override
    public List<DashboardMessageRegionDTO> messageRegion(int top) {
        int limited = Math.max(1, Math.min(MESSAGE_REGION_LIMIT, top));
        List<String> regions = QueryChain.of(portalMessageMapper)
                .select(PORTAL_MESSAGE.REGION)
                .where(PORTAL_MESSAGE.AUDIT_STATUS.eq(MessageAuditStatusEnum.APPROVED.getCode()))
                .listAs(String.class);
        Map<String, Long> counted = regions.stream()
                .map(region -> StrUtil.blankToDefault(region, REGION_UNKNOWN))
                .collect(groupingBy(identity(), LinkedHashMap::new, counting()));
        return counted.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limited)
                .map(entry -> {
                    DashboardMessageRegionDTO item = new DashboardMessageRegionDTO();
                    item.setRegion(entry.getKey());
                    item.setCount(entry.getValue());
                    return item;
                })
                .toList();
    }

    /**
     * {@inheritDoc}
     * <p>
     * 口径为管理端视角（含隐藏内容，登录态可见）：各业务表按创建时间落在统计年份内计数；
     * 当年首次到访城市为当年到访城市集合减去历史到访城市集合；
     * 精选回忆为三类来源各取样 {@value ANNUAL_HIGHLIGHT_LIMIT} 条后合并截取（与时间线同构）。
     */
    @Override
    public AnnualReviewDTO annualReview(int year) {
        int limitedYear = Math.max(1970, Math.min(2100, year));
        LocalDate start = LocalDate.of(limitedYear, 1, 1);
        LocalDate end = start.plusYears(1);
        AnnualReviewDTO review = new AnnualReviewDTO();
        review.setYear(limitedYear);

        // 各模块当年产生量
        review.setDiaryCount(Math.toIntExact(QueryChain.of(portalDiaryMapper)
                .where(PORTAL_DIARY.CREATE_TIME.ge(start.atStartOfDay()))
                .and(PORTAL_DIARY.CREATE_TIME.lt(end.atStartOfDay()))
                .count()));
        review.setMomentsCount(Math.toIntExact(QueryChain.of(portalMomentsMapper)
                .where(PORTAL_MOMENTS.CREATE_TIME.ge(start.atStartOfDay()))
                .and(PORTAL_MOMENTS.CREATE_TIME.lt(end.atStartOfDay()))
                .count()));
        review.setPhotoCount(Math.toIntExact(QueryChain.of(portalLovePhotoMapper)
                .where(PORTAL_LOVE_PHOTO.CREATE_TIME.ge(start.atStartOfDay()))
                .and(PORTAL_LOVE_PHOTO.CREATE_TIME.lt(end.atStartOfDay()))
                .count()));
        review.setFootprintCount(Math.toIntExact(QueryChain.of(portalFootprintMapper)
                .where(PORTAL_FOOTPRINT.ARRIVAL_DATE.ge(start))
                .and(PORTAL_FOOTPRINT.ARRIVAL_DATE.lt(end))
                .count()));

        // 当年首次到访城市（当年到访城市集合 - 历史到访城市集合）
        Set<String> historicCities = new HashSet<>(QueryChain.of(portalFootprintMapper)
                .select(PORTAL_FOOTPRINT.CITY)
                .where(PORTAL_FOOTPRINT.ARRIVAL_DATE.lt(start))
                .listAs(String.class));
        List<String> newCities = QueryChain.of(portalFootprintMapper)
                .select(PORTAL_FOOTPRINT.CITY)
                .where(PORTAL_FOOTPRINT.ARRIVAL_DATE.ge(start))
                .and(PORTAL_FOOTPRINT.ARRIVAL_DATE.lt(end))
                .listAs(String.class).stream()
                .filter(city -> StrUtil.isNotBlank(city) && !historicCities.contains(city))
                .distinct()
                .toList();
        review.setNewCities(newCities);

        // 当年出现最多的心情（sunny/rainy/starry；并列时取先出现者）
        Set<String> moodCodes = Arrays.stream(DiaryMoodEnum.values())
                .map(DiaryMoodEnum::getCode)
                .collect(Collectors.toSet());
        Map<String, Long> moodCount = QueryChain.of(portalDiaryMapper)
                .select(PORTAL_DIARY.MOOD)
                .where(PORTAL_DIARY.CREATE_TIME.ge(start.atStartOfDay()))
                .and(PORTAL_DIARY.CREATE_TIME.lt(end.atStartOfDay()))
                .listAs(String.class).stream()
                .filter(mood -> moodCodes.contains(mood))
                .collect(groupingBy(identity(), LinkedHashMap::new, counting()));
        moodCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(entry -> review.setTopMood(entry.getKey()));

        // 恋爱清单进度（清单无年份维度，取当前累计值）
        review.setLoveListTotal(Math.toIntExact(QueryChain.of(portalLoveListMapper).count()));
        review.setLoveListDone(Math.toIntExact(QueryChain.of(portalLoveListMapper)
                .where(PORTAL_LOVE_LIST.DONE.eq(1))
                .count()));

        // 当年访问量合计
        List<PortalVisit> visits = QueryChain.of(portalVisitMapper)
                .where(PORTAL_VISIT.STAT_DATE.ge(start))
                .and(PORTAL_VISIT.STAT_DATE.lt(end))
                .list();
        review.setPv(visits.stream().mapToLong(visit -> ObjUtil.defaultIfNull(visit.getPv(), 0L)).sum());
        review.setUv(visits.stream().mapToLong(visit -> ObjUtil.defaultIfNull(visit.getUv(), 0L)).sum());

        // 当年精选回忆（三类来源各取样后混排）
        List<DashboardTimelineItemDTO> all = new ArrayList<>();
        all.addAll(queryPhotos(ANNUAL_HIGHLIGHT_LIMIT, start, end));
        all.addAll(queryMoments(ANNUAL_HIGHLIGHT_LIMIT, start, end));
        all.addAll(queryDiary(ANNUAL_HIGHLIGHT_LIMIT, start, end));
        all.sort((a, b) -> b.getTime().compareTo(a.getTime()));
        review.setHighlights(all.subList(0, Math.min(ANNUAL_HIGHLIGHT_LIMIT, all.size())));

        all.sort((a, b) -> b.getTime().compareTo(a.getTime()));
        review.setHighlights(all.subList(0, Math.min(ANNUAL_HIGHLIGHT_LIMIT, all.size())));

        return review;
    }

    /**
     * 查询恋爱画册照片条目。

    /**
     * 查询恋爱画册照片条目。
     *
     * @param limit      取样条数
     * @param startDate  创建时间起始日期（含；null 不过滤）
     * @param endDateExclusive 创建时间截止日期（不含；null 不过滤）
     * @return 照片类时间线条目
     */
    private List<DashboardTimelineItemDTO> queryPhotos(long limit, LocalDate startDate, LocalDate endDateExclusive) {
        return QueryChain.of(portalLovePhotoMapper)
                .select(PORTAL_LOVE_PHOTO.CAPTION, PORTAL_LOVE_PHOTO.URL, PORTAL_LOVE_PHOTO.CREATE_TIME)
                .where(PORTAL_LOVE_PHOTO.CREATE_TIME.ge(startDate == null ? null : startDate.atStartOfDay(), ObjUtil::isNotNull))
                .and(PORTAL_LOVE_PHOTO.CREATE_TIME.lt(endDateExclusive == null ? null : endDateExclusive.atStartOfDay(), ObjUtil::isNotNull))
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
     * @param limit      取样条数
     * @param startDate  创建时间起始日期（含；null 不过滤）
     * @param endDateExclusive 创建时间截止日期（不含；null 不过滤）
     * @return 文章类时间线条目
     */
    private List<DashboardTimelineItemDTO> queryMoments(long limit, LocalDate startDate, LocalDate endDateExclusive) {
        return QueryChain.of(portalMomentsMapper)
                .select(PORTAL_MOMENTS.TITLE, PORTAL_MOMENTS.CONTENT, PORTAL_MOMENTS.CREATE_TIME)
                .where(PORTAL_MOMENTS.CREATE_TIME.ge(startDate == null ? null : startDate.atStartOfDay(), ObjUtil::isNotNull))
                .and(PORTAL_MOMENTS.CREATE_TIME.lt(endDateExclusive == null ? null : endDateExclusive.atStartOfDay(), ObjUtil::isNotNull))
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
     * @param limit      取样条数
     * @param startDate  创建时间起始日期（含；null 不过滤）
     * @param endDateExclusive 创建时间截止日期（不含；null 不过滤）
     * @return 日志类时间线条目
     */
    private List<DashboardTimelineItemDTO> queryDiary(long limit, LocalDate startDate, LocalDate endDateExclusive) {
        return QueryChain.of(portalDiaryMapper)
                .select(PORTAL_DIARY.CONTENT, PORTAL_DIARY.CREATE_TIME)
                .where(PORTAL_DIARY.CREATE_TIME.ge(startDate == null ? null : startDate.atStartOfDay(), ObjUtil::isNotNull))
                .and(PORTAL_DIARY.CREATE_TIME.lt(endDateExclusive == null ? null : endDateExclusive.atStartOfDay(), ObjUtil::isNotNull))
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
