package com.demo.sys.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.sys.datasource.entity.SysMenu;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SysMenuRepository extends CustomerBaseRepository<SysMenu> {


    @Query("""
            SELECT m FROM SysMenu m
            JOIN SysRoleMenu rm ON m.id = rm.menuId
            JOIN SysRole r ON r.id = rm.roleId
             WHERE r.roleKey IN :roleKeys AND m.status = 1 AND m.deleted != 0
             AND  r.status = 1 AND r.deleted != 1
             ORDER BY m.menuSort
            """)
    List<SysMenu> findByRoleKeys(Collection<String> roleKeys);


    @Query("""
            SELECT m FROM SysMenu m
             WHERE m.deleted != 1 ORDER BY m.menuSort
            """)
    List<SysMenu> findByRoot();

}
