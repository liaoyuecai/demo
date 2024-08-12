package com.demo.sys.datasource.dto.page;

import com.demo.core.config.jpa.CustomCriteriaQuery;
import com.demo.core.config.jpa.CustomQuery;
import com.demo.core.config.jpa.QueryCriteria;
import com.demo.core.dto.PageListRequest;
import com.demo.sys.datasource.entity.SysRole;

import java.util.List;

public class RolePageRequest extends PageListRequest<SysRole> {
    @Override
    public CustomCriteriaQuery getCustomCriteriaQuery() {
        return new CustomCriteriaQuery("""
                SELECT r.id,r.role_name,r.description,r.status,r.role_key,
                    GROUP_CONCAT(DISTINCT m.menu_name SEPARATOR ',') as menus 
                    FROM sys_role r 
                    LEFT JOIN sys_role_menu  rm ON r.id = rm.role_id
                    LEFT JOIN sys_menu m ON rm.menu_id= m.id
                    WHERE r.deleted = 0 
                    GROUP BY r.id
                """,
                "SELECT COUNT(1) FROM sys_role r WHERE r.deleted = 0 ",
                List.of(
                        new QueryCriteria("r.roleName", QueryCriteria.Expression.CONTAINS, this.data.getRoleName()),
                        new QueryCriteria("r.roleKey", QueryCriteria.Expression.CONTAINS, this.data.getRoleKey())
                )
        );
    }
}
