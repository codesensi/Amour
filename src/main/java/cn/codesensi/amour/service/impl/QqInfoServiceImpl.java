package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.enums.ConfigKeyEnum;
import cn.codesensi.amour.model.dto.QqInfoResultDTO;
import cn.codesensi.amour.model.entity.SysConfig;
import cn.codesensi.amour.service.QqInfoService;
import cn.codesensi.amour.service.SysConfigService;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * QQ 信息查询服务实现。
 * <p>
 * 通过 Hutool 以服务端身份调用上游接口（不携带浏览器特征请求头，规避上游对浏览器跨域调用的 403 拦截）。
 *
 * @author codesensi
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QqInfoServiceImpl implements QqInfoService {

    private final SysConfigService sysConfigService;

    /**
     * {@inheritDoc}
     */
    @Override
    public QqInfoResultDTO getQqInfo(String qq) {
        SysConfig qqServiceConfig = sysConfigService.oneByKey(ConfigKeyEnum.QQ_SERVICE.getCode());
        if (ObjUtil.isNull(qqServiceConfig) || StrUtil.isBlank(qqServiceConfig.getConfigValue())) {
            log.warn("QQ 信息查询服务未配置");
            return new QqInfoResultDTO();
        }
        String qqServiceUrl = String.format(qqServiceConfig.getConfigValue(), URLEncoder.encode(qq, StandardCharsets.UTF_8));
        String qqServiceResponse = HttpUtil.get(qqServiceUrl);
        log.debug("qq-service {} 响应：{}", qqServiceUrl, qqServiceResponse);
        if (StrUtil.isBlank(qqServiceResponse)) {
            log.warn("QQ 信息查询服务响应为空");
            return new QqInfoResultDTO();
        }
        return JSONUtil.toBean(qqServiceResponse, QqInfoResultDTO.class);
    }

}
