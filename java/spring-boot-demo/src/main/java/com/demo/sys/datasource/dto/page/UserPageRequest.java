package com.demo.sys.datasource.dto.page;

import com.demo.core.config.jpa.CustomCriteriaQuery;
import com.demo.core.config.jpa.QueryCriteria;
import com.demo.core.dto.PageListRequest;
import com.demo.sys.datasource.dto.SysUserDto;

import java.util.List;

public class UserPageRequest extends PageListRequest<SysUserDto> {
    @Override
    public CustomCriteriaQuery getCustomCriteriaQuery() {
        return new CustomCriteriaQuery("""
                SELECT    
                    u.id,   
                    u.real_name,
                    u.username,
                    u.status,
                    u.phone,   
                    GROUP_CONCAT(DISTINCT j.job_name ORDER BY j.job_name SEPARATOR ', ') AS jobs,   
                    GROUP_CONCAT(DISTINCT r.role_name ORDER BY r.role_name SEPARATOR ', ') AS roles   
                FROM    
                    sys_user u   
                LEFT JOIN sys_user_job uj ON u.id = uj.user_id   
                LEFT JOIN sys_job j ON uj.job_id = j.id   
                LEFT JOIN sys_user_role ur ON u.id = ur.user_id   
                LEFT JOIN sys_role r ON ur.role_id = r.id
                """,
                "SELECT COUNT(1) FROM sys_user u ",
                "GROUP BY u.id",
                List.of(
                        new QueryCriteria("u.deleted", QueryCriteria.Expression.EQUALS, 0),
                        new QueryCriteria("u.username", QueryCriteria.Expression.CONTAINS, this.data.getUsername()),
                        new QueryCriteria("u.real_name", QueryCriteria.Expression.CONTAINS, this.data.getRealName()),
                        new QueryCriteria("u.phone", QueryCriteria.Expression.CONTAINS, this.data.getPhone())
                )
        );
    }
}
