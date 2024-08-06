package com.demo.sys.service;

import com.demo.core.exception.ErrorCode;
import com.demo.core.exception.GlobalException;
import com.demo.core.service.impl.DefaultCURDService;
import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.dao.SysMenuRepository;
import com.demo.sys.datasource.dao.SysRoleRepository;
import com.demo.sys.datasource.dao.SysUserRepository;
import com.demo.sys.datasource.dto.ResetPassword;
import com.demo.sys.datasource.dto.WebMenu;
import com.demo.sys.datasource.entity.SysMenu;
import com.demo.sys.datasource.entity.SysRole;
import com.demo.sys.datasource.entity.SysUser;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service("userService")
public class UserService extends DefaultCURDService<SysUser> {

    @Resource
    private SysMenuRepository menuRepository;
    @Resource
    private SysRoleRepository roleRepository;
    @Resource
    private PasswordEncoder passwordEncoder;

    public UserService(@Autowired SysUserRepository repository) {
        super(repository);
    }

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

    public void resetPassword(ResetPassword data, AuthUserCache userDetails) {
        Optional<SysUser> optional = repository.findById(userDetails.getId());
        if (!optional.isPresent()) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR_DATA_NOT_FOUND);
        }
        SysUser user = optional.get();
        if (!passwordEncoder.matches(data.getOldPassword(), user.getPassword())) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR_OLD_PWD);
        }
        user.setPassword(passwordEncoder.encode(data.getNewPassword()));
        user.setUpdateBy(user.getId());
        user.setUpdateTime(LocalDateTime.now());
        repository.save(user);
    }
}
