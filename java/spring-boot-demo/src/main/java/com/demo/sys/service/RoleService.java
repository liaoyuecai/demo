package com.demo.sys.service;

import com.demo.core.service.CURDService;
import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.dao.SysMenuRepository;
import com.demo.sys.datasource.dao.SysRoleMenuRepository;
import com.demo.sys.datasource.dao.SysRoleRepository;
import com.demo.sys.datasource.dto.RoleBindMenu;
import com.demo.sys.datasource.entity.SysRole;
import com.demo.sys.datasource.entity.SysRoleMenu;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("roleService")
public class RoleService extends CURDService<SysRole, SysRoleRepository> {

    @Resource
    SysRoleMenuRepository roleMenuRepository;
    @Resource
    SysMenuRepository menuRepository;


    public RoleService(@Autowired SysRoleRepository repository) {
        super(repository);
    }

    @Override
    public void deleteUpdate(Integer id) {
        //todo 验证用户绑定角色
        super.deleteUpdate(id);
    }

    public void bindMenu(RoleBindMenu menu, AuthUserCache user) {
        //todo 校验角色
        roleMenuRepository.deleteByRoleId(menu.getRoleId());
        roleMenuRepository.saveAll(menu.getMenuIds().stream()
                .map(i->new SysRoleMenu(menu.getRoleId(),i)).toList());
    }

    public List<Integer> findBindMenusId(Integer roleId) {
        return menuRepository.findIdByRoleId(roleId);
    }
}
