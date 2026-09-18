package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.CacheConst;
import cn.codesensi.amour.common.consts.RegexConst;
import cn.codesensi.amour.common.enums.BaseEnum;
import cn.codesensi.amour.common.enums.CacheNameEnum;
import cn.codesensi.amour.common.enums.ValueType;
import cn.codesensi.amour.common.enums.YesEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.exception.ValidationException;
import cn.codesensi.amour.common.util.CacheUtil;
import cn.codesensi.amour.mapper.SysConfigMapper;
import cn.codesensi.amour.model.converter.ConfigConverter;
import cn.codesensi.amour.model.dto.ConfigDTO;
import cn.codesensi.amour.model.dto.ConfigPageDTO;
import cn.codesensi.amour.model.dto.ConfigUpdateDTO;
import cn.codesensi.amour.model.entity.SysConfig;
import cn.codesensi.amour.service.CacheEvictService;
import cn.codesensi.amour.service.SysConfigService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.codesensi.amour.model.entity.table.SysConfigTableDef.SYS_CONFIG;

/**
 * 运行时配置查询服务实现。
 * <p>
 * 优先从 Caffeine 缓存（配置名 {@code config}，见 {@link CacheNameEnum#CONFIG}）读取以点分路径
 * （如 {@code name}、{@code captcha.enabled}）作为 {@code config_key} 存储的配置，未命中时回源查库并回填，
 * 减少高频配置点的数据库压力。
 * <p>
 * 缓存采用"驻留不自动过期"策略，热更新依赖写库侧显式失效对应配置键
 * （经 {@link CacheEvictService#evictConfigCache(List)}）；在缓存未就绪或回源异常时
 * 降级为直接查库，保证配置读取不受缓存故障影响。
 * <p>
 * 查询结果以 {@link ConfigDTO} 返回；当配置键在库中不存在时，
 * 结果中不包含对应条目，避免调用侧因缺配置而失败。
 * <p>
 * 同时承载管理端能力：分页查询（直查库）与修改配置
 * （仅配置值，修改后失效对应配置键的缓存，实现热更新）。
 *
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    /**
     * DATETIME 值类型的合法形态（yyyy-MM-dd HH:mm:ss），与门户展示契约一致
     */
    private static final Pattern DATETIME_PATTERN = Pattern.compile(RegexConst.DIGITS_4_2_2_SPACE_2_2_2);

    private final SysConfigMapper sysConfigMapper;
    private final CacheManager cacheManager;
    private final ConfigConverter configConverter;
    private final CacheEvictService cacheEvictService;

    /**
     * 从 config 缓存读取指定配置键的配置；未命中时回源查库并回填缓存。
     *
     * @param key 配置键
     * @return 配置实体；不存在返回 {@code null}
     */
    @Override
    public SysConfig oneByKey(String key) {
        // 统一缓存读取：原子回源 + 空值哨兵 + 缓存故障降级（见 CacheUtil#load）
        return CacheUtil.load(cacheManager, CacheNameEnum.CONFIG.getCode(), key, k -> {
            SysConfig config = oneByKeyDb(k);
            log.debug("config 缓存回源查库：key={}，result={}", k, ObjUtil.isNull(config) ? "不存在，以空值哨兵占位" : "已加载");
            return config;
        });
    }

    /**
     * 按配置键集合批量查询配置；入参为空（{@code null} 或不含元素）时返回空列表。
     * <p>
     * 命中缓存的键直接取用，未命中的合并为一条 IN 查询回源并逐键回填
     * （不存在的键以空值哨兵占位，防穿透，语义与 {@link #oneByKey} 对齐），
     * 不存在的配置键不出现在结果中，集合中的 {@code null} 元素会被跳过。
     *
     * @param keys 待查询的配置键集合（app 之下的点分路径）；为空时返回空列表
     * @return 配置 DTO 列表；无命中时返回空列表
     */
    @Override
    public List<ConfigDTO> listByKeys(List<String> keys) {
        if (CollUtil.isEmpty(keys)) {
            return List.of();
        }
        // 待回源键去重（保序、跳过 null）
        List<String> distinctKeys = keys.stream().filter(Objects::nonNull).distinct().toList();
        if (CollUtil.isEmpty(distinctKeys)) {
            return List.of();
        }
        Cache cache = cacheManager.getCache(CacheUtil.withAppEnv(CacheNameEnum.CONFIG.getCode()));
        if (ObjUtil.isNull(cache)) {
            // 缓存未注册/未就绪：降级为一条 IN 查询直查库
            log.debug("config 缓存未注册，降级为直接查库：keys={}", distinctKeys);
            return listByKeysDb(distinctKeys).values().stream().map(configConverter::toDTO).toList();
        }
        Map<String, SysConfig> loaded = new LinkedHashMap<>();
        List<String> missingKeys = new ArrayList<>();
        // 1. 逐键命中缓存，收集未命中键（空值哨兵表示"该键确认不存在"，不算未命中）
        for (String key : distinctKeys) {
            Cache.ValueWrapper wrapper = cache.get(key);
            if (ObjUtil.isNull(wrapper)) {
                missingKeys.add(key);
                continue;
            }
            Object cached = wrapper.get();
            if (ObjUtil.isNotNull(cached) && cached != CacheConst.NULL_MARKER) {
                loaded.put(key, (SysConfig) cached);
            }
        }
        // 2. 未命中键合并一条 IN 查询回源，并逐键回填（不存在的键以空值哨兵占位，防穿透）
        if (CollUtil.isNotEmpty(missingKeys)) {
            Map<String, SysConfig> fromDb = listByKeysDb(missingKeys);
            for (String key : missingKeys) {
                SysConfig config = fromDb.get(key);
                cache.put(key, ObjUtil.defaultIfNull(config, CacheConst.NULL_MARKER));
                if (ObjUtil.isNotNull(config)) {
                    loaded.put(key, config);
                }
            }
        }
        return loaded.values()
                .stream()
                .map(configConverter::toDTO)
                .toList();
    }

    /**
     * 从 sys_config 表按配置键集合批量查询配置记录（一条 IN 查询）。
     *
     * @param keys 配置键集合（去重后非空）
     * @return 配置键 -> 配置实体
     */
    private Map<String, SysConfig> listByKeysDb(List<String> keys) {
        return QueryChain.of(sysConfigMapper)
                .where(SYS_CONFIG.CONFIG_KEY.in(keys))
                .list()
                .stream()
                .collect(Collectors.toMap(SysConfig::getConfigKey, config -> config,
                        (first, second) -> first, LinkedHashMap::new));
    }

    /**
     * 按配置键集合查询可公开下发的配置（免登录配置下发接口专用）。
     * <p>
     * 在 {@link #listByKeys} 结果基础上剔除敏感配置（{@code sensitive=1}），
     * 敏感键在响应中的表现与"键不存在"一致，不暴露其存在性；
     * 服务端内部消费敏感配置（如 uapi-key）请走 {@link #oneByKey}。
     *
     * @param keys 待查询的配置键集合；为空时返回空列表
     * @return 可公开下发的配置 DTO 列表；无命中时返回空列表
     */
    @Override
    public List<ConfigDTO> listByKeysPublic(List<String> keys) {
        return listByKeys(keys).stream()
                .filter(config -> YesEnum.NO.getCode().equals(config.getSensitive()))
                .toList();
    }

    /**
     * 分页查询配置（管理端，完整字段）。
     * <p>
     * 配置键为模糊匹配，分组为精确匹配，条件缺省时自动忽略；
     * 排序为分组升序 → id 升序（与初始化数据的分组分段一致）；
     * 页码与每页条数的缺省值由 {@link cn.codesensi.amour.common.core.BasePage} 提供（1 与 20）。
     * <p>
     * 管理端直查数据库、不走 config 缓存，看到的是真实库态；
     * 逻辑删除（del_flag）由 MyBatis-Flex 全局配置自动追加过滤。
     *
     * @param pageDTO 分页查询参数
     * @return 配置实体分页结果
     */
    @Override
    public Page<SysConfig> page(ConfigPageDTO pageDTO) {
        return QueryChain.of(sysConfigMapper)
                .select(SYS_CONFIG.ALL_COLUMNS)
                .where(SYS_CONFIG.CONFIG_KEY.like(pageDTO.getConfigKey(), StrUtil::isNotBlank))
                .and(SYS_CONFIG.CONFIG_GROUP.eq(pageDTO.getConfigGroup(), StrUtil::isNotBlank))
                .orderBy(SYS_CONFIG.CONFIG_GROUP, true)
                .orderBy(SYS_CONFIG.ID, true)
                .page(Page.of(pageDTO.getPageNumber(), pageDTO.getPageSize()));
    }

    /**
     * 修改配置：仅允许修改配置值，更新成功后失效该配置键的缓存实现热更新。
     * <p>
     * 配置值会按该记录的 {@code value_type} 做格式校验，避免写入与类型不符的脏值
     * 导致调用侧（含运行时类型转换）失败；配置键、值类型、分组与状态由代码侧约定，不接受修改。
     * <p>
     * 缓存失效注册在事务提交后执行，避免提交前其他请求回源查库把中间状态重新写入缓存；
     * 空值哨兵（配置此前被查询过但不存在时写入）同样随缓存失效一并清除。
     *
     * @param updateDTO 修改参数
     */
    @Override
    public void update(ConfigUpdateDTO updateDTO) {
        SysConfig config = QueryChain.of(sysConfigMapper)
                .select(SYS_CONFIG.ALL_COLUMNS)
                .where(SYS_CONFIG.ID.eq(updateDTO.getId()))
                .one();
        if (ObjUtil.isNull(config)) {
            throw new BusinessException("配置不存在");
        }

        validateValueByType(config.getValueType(), updateDTO.getConfigValue());

        // 仅更新配置值，config_key/value_type/config_group/status/remark 均不可变更；
        // 经 UpdateChain 显式逐列赋值，配置值置空时写入 null（清空即回退消费端兜底值）
        UpdateChain.of(SysConfig.class)
                .set(SYS_CONFIG.CONFIG_VALUE, updateDTO.getConfigValue())
                .where(SYS_CONFIG.ID.eq(updateDTO.getId()))
                .update();

        CacheUtil.evictAfterCommit(() -> {
            log.debug("配置修改完成，失效缓存：configKey={}", config.getConfigKey());
            cacheEvictService.evictConfigCache(List.of(config.getConfigKey()));
        });
    }

    /**
     * 按值类型校验配置值格式。
     * <p>
     * {@code BOOLEAN} 仅接受 {@code true}/{@code false}（与前端归一化 {@code === "true"} 的判定一致），
     * {@code INTEGER}/{@code LONG} 须为整数；STRING 及未知类型不做格式限制。
     *
     * @param valueType 值类型
     * @param value     配置值
     */
    private void validateValueByType(String valueType, String value) {
        // 未知/空类型按 STRING 口径处理（不做格式限制），与既有行为一致
        ValueType type = BaseEnum.fromCode(ValueType.class, valueType);
        switch (ObjUtil.defaultIfNull(type, ValueType.STRING)) {
            case BOOLEAN -> {
                if (!"true".equals(value) && !"false".equals(value)) {
                    throw new ValidationException("布尔型配置值只能为 true 或 false");
                }
            }
            case INTEGER -> {
                if (!NumberUtil.isInteger(value)) {
                    throw new ValidationException("整型配置值必须为整数");
                }
            }
            case LONG -> {
                if (!NumberUtil.isLong(value)) {
                    throw new ValidationException("长整型配置值必须为整数");
                }
            }
            case DATETIME -> {
                // 先核对 yyyy-MM-dd HH:mm:ss 形态，再严格解析拦截不存在的日期（如 2 月 30 日）
                if (ObjUtil.isNull(value) || !DATETIME_PATTERN.matcher(value).matches()) {
                    throw new ValidationException("日期时间格式必须为 yyyy-MM-dd HH:mm:ss");
                }
                try {
                    LocalDateTime.parse(value, DatePattern.NORM_DATETIME_FORMATTER);
                } catch (DateTimeParseException e) {
                    throw new ValidationException("日期时间值不存在，请重新选择");
                }
            }
            default -> {
                // STRING 及未知类型不做格式校验
            }
        }
    }

    /**
     * 从 sys_config 表查询指定配置键的配置记录。
     *
     * @param key 配置键
     * @return 配置实体；不存在返回 {@code null}
     */
    private SysConfig oneByKeyDb(String key) {
        return QueryChain.of(sysConfigMapper)
                .where(SYS_CONFIG.CONFIG_KEY.eq(key))
                .one();
    }
}
