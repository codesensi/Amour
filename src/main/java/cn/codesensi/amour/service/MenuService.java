package cn.codesensi.amour.service;

import cn.codesensi.amour.model.entity.SysMenu;
import com.mybatisflex.core.service.IService;

/**
 * 菜单 Service。
 * <p>
 * 继承 MyBatis-Flex 的 {@link IService}，开箱即得 {@code sys_menu} 表的增删改查能力；
 * 业务方法按需在后续迭代中补充。
 */
public interface MenuService extends IService<SysMenu> {
}
