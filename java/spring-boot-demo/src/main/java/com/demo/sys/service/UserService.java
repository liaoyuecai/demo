package com.demo.sys.service;

import com.demo.core.service.impl.DefaultCURDService;
import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.dao.SysMenuRepository;
import com.demo.sys.datasource.dao.SysRoleRepository;
import com.demo.sys.datasource.dto.WebMenu;
import com.demo.sys.datasource.entity.SysMenu;
import com.demo.sys.datasource.entity.SysRole;
import com.demo.sys.datasource.entity.SysUser;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("userService")
public class UserService extends DefaultCURDService<SysUser> {
    @Resource
    private SysMenuRepository menuRepository;
    @Resource
    private SysRoleRepository roleRepository;

    /**
     * 完善当前用户缓存信息
     * 完善用户菜单、角色等
     *
     * @param userCache
     */
    public void currentUser(AuthUserCache userCache) {
        if (userCache.isRoot()) {
            List<SysMenu> menus = menuRepository.findByRoot();
            if (menus != null) userCache.setMenuData(getMenuTree(menus));
        }
        if (userCache.getRoles() != null && !userCache.getRoles().isEmpty()) {
            List<SysMenu> menus = menuRepository.findByRoleKeys(
                    userCache.getRoles().stream().map(SysRole::getRoleKey).toList());
            if (menus != null) userCache.setMenuData(getMenuTree(menus));
        }
    }

    /**
     * 转换树形
     *
     * @param menus
     * @return
     */
    List<WebMenu> getMenuTree(List<SysMenu> menus) {
        Map<Integer, WebMenu> nodeMap = new HashMap<>();
        List<WebMenu> rootNodes = new ArrayList<>();
        List<WebMenu> children = new ArrayList<>();
        for (SysMenu menu : menus) {
            WebMenu node = new WebMenu(menu);
            nodeMap.put(menu.getId(), node);
            if (node.getParentId() == null) rootNodes.add(node);
            else children.add(node);
        }
        children.forEach(node -> {
            if (node.getParentId() != null)
                nodeMap.get(node.getParentId()).getChildren().add(node);
        });
        return rootNodes;
    }

}
