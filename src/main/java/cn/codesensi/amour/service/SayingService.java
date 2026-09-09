package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.SayingResultDTO;

/**
 * 一言查询服务。
 *
 * @author codesensi
 * @since 1.0
 */
public interface SayingService {

    /**
     * 获取一言（随机优先，失败降级）。
     * <p>
     * 优先调用随机一言接口（uapi-saying-random），失败降级一言接口（uapi-saying），
     * 两级上游均不可用时返回空 DTO。
     *
     * @return 一言数据；降级时出处与作者为空，两级上游均不可用时字段全为 null
     */
    SayingResultDTO getSaying();

}
