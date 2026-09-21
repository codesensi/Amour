package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.VisitTotalDTO;

/**
 * 门户访问统计 Service —— 门户访问量的上报与累计查询。
 *
 * @author codesensi
 * @since 1.0
 */
public interface PortalVisitService {

    /**
     * 上报一次访问：当日统计行 PV/UV 原子自增，当日无记录时初始化。
     * 去重由前端按「访客 + 日」控制（每浏览器每日仅上报一次）。
     */
    void report();

    /**
     * 查询累计访问统计（全部日粒度记录的累计）。
     *
     * @return 累计 PV 与 UV
     */
    VisitTotalDTO total();

}
