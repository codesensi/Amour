package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.consts.AppConst;
import cn.codesensi.amour.mapper.PortalVisitMapper;
import cn.codesensi.amour.model.dto.VisitTotalDTO;
import cn.codesensi.amour.model.entity.PortalVisit;
import cn.codesensi.amour.service.PortalVisitService;
import cn.hutool.core.util.ObjUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static cn.codesensi.amour.model.entity.table.PortalVisitTableDef.PORTAL_VISIT;
import static com.mybatisflex.core.query.QueryMethods.sum;

/**
 * 门户访问统计 Service 实现。
 * <p>
 * 数据量级为日粒度（每年数百行），累计查询直接聚合，无需额外缓存。
 *
 * @author codesensi
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class PortalVisitServiceImpl implements PortalVisitService {

    private final PortalVisitMapper portalVisitMapper;

    /**
     * 上报一次访问：先对当日行原子自增（pv = pv + 1），无行时插入初始行。
     * <p>
     * UV 去重由前端保证（每浏览器每日仅上报一次，localStorage 按日标记）；
     * 先更后插的并发窗口极小，极端情况下出现的重复日期行仅影响展示精度，不影响功能。
     */
    @Override
    public void report() {
        LocalDate today = LocalDate.now();
        boolean updated = UpdateChain.of(PortalVisit.class)
                .setRaw(PORTAL_VISIT.PV, PORTAL_VISIT.PV.add(AppConst.ONE_LONG))
                .setRaw(PORTAL_VISIT.UV, PORTAL_VISIT.UV.add(AppConst.ONE_LONG))
                .where(PORTAL_VISIT.STAT_DATE.eq(today))
                .update();
        if (!updated) {
            PortalVisit entity = new PortalVisit()
                    .setStatDate(today)
                    .setPv(AppConst.ONE_LONG)
                    .setUv(AppConst.ONE_LONG);
            portalVisitMapper.insert(entity);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * 聚合下推数据库一次求和，SUM 结果（BIGINT）以别名列直接投影到统计 DTO 的
     * Long 字段承接——别名经方法引用绑定 DTO 属性（编译期校验，无魔法值），
     * 不经实体的 Integer 字段中转，避免类型转换静默失败恒为空；
     * 逻辑删除（del_flag）由 MyBatis-Flex 全局配置自动追加过滤。
     */
    @Override
    public VisitTotalDTO total() {
        VisitTotalDTO dto = QueryChain.of(portalVisitMapper)
                .select(
                        sum(PORTAL_VISIT.PV).as(VisitTotalDTO::getPv),
                        sum(PORTAL_VISIT.UV).as(VisitTotalDTO::getUv)
                )
                .oneAs(VisitTotalDTO.class);
        if (ObjUtil.isNull(dto)) {
            dto = new VisitTotalDTO();
        }
        dto.setPv(ObjUtil.defaultIfNull(dto.getPv(), AppConst.ZERO_LONG));
        dto.setUv(ObjUtil.defaultIfNull(dto.getUv(), AppConst.ZERO_LONG));
        return dto;
    }

}
