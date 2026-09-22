package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.enums.HiddenEnum;
import cn.codesensi.amour.mapper.PortalAnniversaryMapper;
import cn.codesensi.amour.mapper.PortalFootprintMapper;
import cn.codesensi.amour.mapper.PortalLovePhotoMapper;
import cn.codesensi.amour.mapper.PortalMessageMapper;
import cn.codesensi.amour.model.dto.DashboardSummaryDTO;
import cn.codesensi.amour.model.dto.DashboardTimelineDTO;
import cn.codesensi.amour.model.dto.DashboardTimelineItemDTO;
import cn.codesensi.amour.model.entity.PortalAnniversary;
import cn.codesensi.amour.model.entity.PortalMessage;
import cn.codesensi.amour.service.AnniversaryService;
import cn.codesensi.amour.service.DashboardService;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.codesensi.amour.model.entity.table.PortalAnniversaryTableDef.PORTAL_ANNIVERSARY;
import static cn.codesensi.amour.model.entity.table.PortalFootprintTableDef.PORTAL_FOOTPRINT;
import static cn.codesensi.amour.model.entity.table.PortalLovePhotoTableDef.PORTAL_LOVE_PHOTO;
import static cn.codesensi.amour.model.entity.table.PortalMessageTableDef.PORTAL_MESSAGE;

/**
 * 管理端首页数据聚合 Service 实现。
 * <p>
 * 取数策略按模块落地情况分层：已落地模块（画册/纪念日/足迹/留言簿）复用实体 Mapper 走
 * MyBatis-Flex 强类型查询（逻辑删除由 {@code logic-delete-column} 全局配置自动过滤）；
 * 模块后端尚未实现的表（点点滴滴/情侣日志/恋爱清单/时间胶囊）暂以
 * {@link JdbcTemplate} 原生 SQL 只读统计，待对应模块落地后迁移至实体查询。
 * <p>
 * 原生 SQL 中的表名为类内硬编码白名单，不存在注入面。
 *
 * @author codesensi
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    /**
     * 仅为无实体模块提供原生只读统计
     */
    private final JdbcTemplate jdbcTemplate;
    private final PortalAnniversaryMapper portalAnniversaryMapper;
    private final PortalLovePhotoMapper portalLovePhotoMapper;
    private final PortalFootprintMapper portalFootprintMapper;
    private final PortalMessageMapper portalMessageMapper;
    private final AnniversaryService anniversaryService;

    /**
     * 时间线展示的时间格式（定长字符串字典序即时间序，便于混排比较）
     */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

        // 各模块条目计数：已落地模块走实体计数，未落地模块走原生统计
        DashboardSummaryDTO.ModuleCounts counts = new DashboardSummaryDTO.ModuleCounts();
        counts.setAnniversaries(Math.toIntExact(QueryChain.of(portalAnniversaryMapper).count()));
        counts.setMoments(countTable("portal_moments"));
        counts.setPhotos(Math.toIntExact(QueryChain.of(portalLovePhotoMapper).count()));
        counts.setMessages(Math.toIntExact(QueryChain.of(portalMessageMapper).count()));
        counts.setDiaries(countTable("portal_diary"));
        counts.setTimeCapsules(countTable("portal_time_capsule"));
        counts.setFootprints(Math.toIntExact(QueryChain.of(portalFootprintMapper).count()));
        summary.setCounts(counts);

        // 恋爱清单进度
        DashboardSummaryDTO.LoveListProgress progress = new DashboardSummaryDTO.LoveListProgress();
        progress.setTotal(countTable("portal_love_list"));
        progress.setDone(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM `portal_love_list` WHERE `del_flag` = 0 AND `done` = 1", Long.class).intValue());
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

        // 最新一条留言（迁移自原生 SQL，口径保持「未删除全部」：管理端首页需感知待审核留言）
        PortalMessage latest = QueryChain.of(portalMessageMapper)
                .select(PORTAL_MESSAGE.NICKNAME, PORTAL_MESSAGE.CONTENT, PORTAL_MESSAGE.CREATE_TIME)
                .orderBy(PORTAL_MESSAGE.CREATE_TIME, false)
                .limit(1)
                .one();
        if (ObjUtil.isNotNull(latest)) {
            DashboardSummaryDTO.LatestMessage latestMessage = new DashboardSummaryDTO.LatestMessage();
            latestMessage.setNickname(latest.getNickname());
            latestMessage.setContent(latest.getContent());
            latestMessage.setCreateTime(formatDateTime(latest.getCreateTime()));
            summary.setLatestMessage(latestMessage);
        }

        return summary;
    }

    /**
     * 最近回忆时间线：三类来源各自取「页码 × 每页条数」条，Java 合并排序后内存分页。
     * <p>
     * 混排时间统一取各表 {@code create_time}（记录业务日期可能为空，不参与排序），
     * 格式化为定长字符串后字典序即时间序。
     */
    @Override
    public Page<DashboardTimelineItemDTO> timeline(DashboardTimelineDTO timelineDTO) {
        long pageNumber = timelineDTO.getPageNumber();
        long pageSize = timelineDTO.getPageSize();
        long fetchLimit = pageNumber * pageSize;
        List<DashboardTimelineItemDTO> all = new ArrayList<>();
        all.addAll(queryPhotos(fetchLimit));
        all.addAll(queryMoments(fetchLimit));
        all.addAll(queryDiary(fetchLimit));
        all.sort((a, b) -> b.getTime().compareTo(a.getTime()));

        long total = QueryChain.of(portalLovePhotoMapper).count()
                + countTable("portal_moments")
                + countTable("portal_diary");
        long fromIndex = (pageNumber - 1) * pageSize;
        long toIndex = Math.min(fromIndex + pageSize, all.size());
        List<DashboardTimelineItemDTO> records = fromIndex >= all.size()
                ? List.of()
                : all.subList((int) fromIndex, (int) toIndex);
        return new Page<>(records, pageNumber, pageSize, total);
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
                    item.setTime(formatDateTime(photo.getCreateTime()));
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
        String sql = "SELECT `title`, SUBSTRING(`content`, 1, 200) AS `summary`, `create_time` FROM `portal_moments` "
                + "WHERE `del_flag` = 0 ORDER BY `create_time` DESC LIMIT ?";
        return jdbcTemplate.queryForList(sql, limit).stream()
                .map(row -> {
                    DashboardTimelineItemDTO item = new DashboardTimelineItemDTO();
                    item.setType(DashboardTimelineItemDTO.TimelineItemType.moments);
                    item.setTime(formatDateTime(row.get("create_time")));
                    item.setTitle((String) row.get("title"));
                    item.setContent((String) row.get("summary"));
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
        String sql = "SELECT SUBSTRING(`content`, 1, 200) AS `summary`, `create_time` FROM `portal_diary` "
                + "WHERE `del_flag` = 0 ORDER BY `create_time` DESC LIMIT ?";
        return jdbcTemplate.queryForList(sql, limit).stream()
                .map(row -> {
                    DashboardTimelineItemDTO item = new DashboardTimelineItemDTO();
                    item.setType(DashboardTimelineItemDTO.TimelineItemType.diary);
                    item.setTime(formatDateTime(row.get("create_time")));
                    item.setTitle("情侣日志");
                    item.setContent((String) row.get("summary"));
                    return item;
                })
                .toList();
    }

    /**
     * 统计表内未删除记录数。
     *
     * @param tableName 表名（仅限本类内硬编码白名单）
     * @return 未删除记录数
     */
    private int countTable(String tableName) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM `" + tableName + "` WHERE `del_flag` = 0", Long.class);
        return count == null ? 0 : count.intValue();
    }

    /**
     * 格式化时间为展示文本。
     *
     * @param value 时间值（数据库原生为 {@link java.sql.Timestamp}，实体路径为 {@link LocalDateTime}）
     * @return yyyy-MM-dd HH:mm 文本；无法识别时返回 null
     */
    private String formatDateTime(Object value) {
        LocalDateTime dateTime = null;
        if (value instanceof java.sql.Timestamp timestamp) {
            dateTime = timestamp.toLocalDateTime();
        } else if (value instanceof LocalDateTime localDateTime) {
            dateTime = localDateTime;
        }
        return ObjUtil.isNull(dateTime) ? null : TIME_FORMATTER.format(dateTime);
    }

}
