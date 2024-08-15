package com.demo.sys.service;

import com.demo.core.service.CURDService;
import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.dao.SysMenuRepository;
import com.demo.sys.datasource.entity.SysMenu;
import com.demo.sys.datasource.entity.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("menuService")
public class MenuService extends CURDService<SysMenu, SysMenuRepository> {


    public MenuService(@Autowired SysMenuRepository repository) {
        super(repository);
    }



    public List<SysMenu> findOwnList(AuthUserCache userCache) {
        if (userCache.isRoot())
            return repository.findByRoot();
        if (userCache.getRoleList() != null && !userCache.getRoleList().isEmpty()) {
            return repository.findByRoleId(
                    userCache.getRoleList().stream().map(SysRole::getId).toList());
        }
        return List.of();
    }
}
