package com.demo.sys.datasource.dto.page;

import com.demo.core.config.jpa.CustomCriteriaQuery;
import com.demo.core.config.jpa.QueryCriteria;
import com.demo.core.dto.PageListRequest;
import com.demo.sys.datasource.entity.SysRole;

import java.util.List;

public class RolePageRequest extends PageListRequest<SysRole> {
    @Override
    public CustomCriteriaQuery getCustomCriteriaQuery() {
        String querySql = """
                SELECT r.id,r.role_name,r.role_type,r.description,r.status,r.role_key,
                    GROUP_CONCAT(DISTINCT m.menu_name SEPARATOR ',') as menus 
                    FROM sys_role r 
                    LEFT JOIN sys_role_menu  rm ON r.id = rm.role_id
                    LEFT JOIN sys_menu m ON rm.menu_id= m.id
                """;
        String countSql = "SELECT COUNT(1) FROM sys_role r ";
        String groupBy = " GROUP BY r.id";
        List<QueryCriteria> params = List.of(
                new QueryCriteria("r.deleted", QueryCriteria.Expression.EQUALS, 0),
                new QueryCriteria("r.roleName", QueryCriteria.Expression.CONTAINS, this.data.getRoleName()),
                new QueryCriteria("r.roleKey", QueryCriteria.Expression.CONTAINS, this.data.getRoleKey())
        );
        if (!this.user.isRoot()) {
            //非超管用户只显示自己创建和公用角色
            params.add(new QueryCriteria("", QueryCriteria.Expression.OR, List.of(
                    new QueryCriteria("r.role_type", QueryCriteria.Expression.EQUALS, 1),
                    new QueryCriteria("", QueryCriteria.Expression.AND, List.of(
                            new QueryCriteria("r.role_type", QueryCriteria.Expression.EQUALS, 2),
                            new QueryCriteria("r.create_by", QueryCriteria.Expression.EQUALS, this.user.getId())
                    ))
            )));
        }
        return new CustomCriteriaQuery(querySql, countSql, groupBy, params);
    }
}
