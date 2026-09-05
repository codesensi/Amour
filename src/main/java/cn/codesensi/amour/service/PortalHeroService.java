package cn.codesensi.amour.service;

import cn.codesensi.amour.model.dto.PortalHeroResultDTO;

/**
 * 门户主角服务。
 *
 * @author codesensi
 * @since 1.0
 */
public interface PortalHeroService {

    /**
     * 查询门户男女主展示信息。
     * <p>
     * 男女主取自绑定了 hero 角色的启用用户：同性别按ID倒序取最晚注册的一个；
     * 角色不存在或该性别暂无用户时对应字段返回 null，由前端兜底。
     *
     * @return 男主与女主信息
     */
    PortalHeroResultDTO getPortalHero();

}
