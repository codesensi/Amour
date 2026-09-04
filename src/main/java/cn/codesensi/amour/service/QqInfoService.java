package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.QqInfoResultDTO;

/**
 * QQ 信息查询服务。
 *
 * @author codesensi
 * @since 1.0
 */
public interface QqInfoService {

    /**
     * 按 QQ 号查询头像地址与昵称。
     * <p>
     * 查询结果携带 10 分钟缓存；上游失败时的降级策略见接口说明。
     *
     * @param qq QQ 号（6~12 位数字，由控制器完成格式校验）
     * @return 头像地址与昵称；昵称仅 qq-service 解析成功时非空
     * @throws cn.codesensi.amour.common.exception.SystemException 两层服务均不可用时抛出
     */
    QqInfoResultDTO getQqInfo(String qq);

}
