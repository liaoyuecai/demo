package com.demo.sys.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.sys.datasource.entity.SysRoleMenu;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SysRoleMenuRepository extends CustomerBaseRepository<SysRoleMenu> {
    List<SysRoleMenu> findByRoleId(Integer roleId);

    List<SysRoleMenu> findByRoleIdIn(Collection<Integer> roleIds);

    long deleteByRoleId(int roleId);

    void deleteByMenuIdIn(Collection<Integer> menus);
}
