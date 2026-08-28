package cn.codesensi.amour.service;

import cn.codesensi.amour.model.response.CacheResponse;

import java.util.List;

/**
 * 缓存查询接口。
 *
 * @author codesensi
 * @since 1.0
 */
public interface CacheService {

    /**
     * 查询全部 Caffeine 缓存内容。
     *
     * @return 各缓存的名称与条目列表；无缓存时返回空列表
     */
    List<CacheResponse> listAll();
}
