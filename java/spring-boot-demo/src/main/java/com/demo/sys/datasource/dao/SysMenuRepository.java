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
             WHERE r.id IN :roleIds AND m.status = 1 AND m.deleted != 1
             AND  r.status = 1 AND r.deleted != 1
             ORDER BY m.menuSort
            """)
    List<SysMenu> findByRoleId(Collection<Integer> roleIds);


    @Query("""
            SELECT m FROM SysMenu m
             WHERE m.deleted != 1 ORDER BY m.menuSort
            """)
    List<SysMenu> findByRoot();


    @Query("""
            SELECT m.id FROM SysMenu m
            JOIN SysRoleMenu rm ON m.id = rm.menuId
            WHERE rm.roleId = :roleId AND m.status = 1 AND m.deleted != 1
            """)
    List<Integer> findIdByRoleId(Integer roleId);

}
