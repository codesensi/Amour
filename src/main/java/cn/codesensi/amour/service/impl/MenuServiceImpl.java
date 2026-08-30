package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.model.entity.SysMenu;
import cn.codesensi.amour.mapper.SysMenuMapper;
import cn.codesensi.amour.service.MenuService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 菜单 Service 实现。
 * <p>
 * CRUD 能力由 MyBatis-Flex 的 {@link ServiceImpl} 统一提供。
 */
@Service
public class MenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements MenuService {
}
