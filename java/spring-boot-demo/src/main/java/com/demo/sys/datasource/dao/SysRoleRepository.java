package com.demo.sys.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.sys.datasource.entity.SysRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysRoleRepository extends CustomerBaseRepository<SysRole> {
    @Query(value = """
                    SELECT r  FROM SysUser u JOIN SysUserRole ur ON u.id = ur.userId
                    JOIN SysRole r ON ur.roleId = r.id 
                    WHERE u.id =  :userId and r.deleted = 0 and r.status = 1
            """)
    List<SysRole> findRolesByUserId(@Param("userId") int userId);

}
