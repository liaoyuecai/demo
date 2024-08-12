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

    @Override
    public void deleteUpdate(Integer id) {
        //todo 后期要验证角色关联
        super.deleteUpdate(id);
    }


    public List<SysMenu> findOwnList(AuthUserCache userCache) {
        if (userCache.isRoot())
            return repository.findByRoot();
        if (userCache.getRoles() != null && !userCache.getRoles().isEmpty()) {
            return repository.findByRoleKeys(
                    userCache.getRoles().stream().map(SysRole::getRoleKey).toList());
        }
        return List.of();
    }
}
