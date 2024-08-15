package com.demo.sys.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.sys.datasource.entity.SysRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysRoleRepository extends CustomerBaseRepository<SysRole> {
    @Query(value = """
            SELECT r  FROM SysRole r JOIN SysUserRole ur ON r.id = ur.roleId
            JOIN SysUser u ON ur.userId = u.id 
            WHERE u.id =  :userId and r.deleted = 0 and r.status = 1
            """)
    List<SysRole> findRolesByUserId(@Param("userId") int userId);

    @Query(value = """
            SELECT r  FROM SysRole r 
            WHERE  r.deleted = 0 and r.status = 1 and 
            (r.roleType = 1 or (r.roleType = 2 and r.createBy = :userId))
            """)
    List<SysRole> findByUser(Integer userId);

    @Query(value = """
            SELECT r  FROM SysRole r 
            WHERE  r.deleted = 0 and r.status = 1 and 
            (r.roleType = 1 or (r.roleType = 2 and r.createBy = :userId))
            """)
    Page<SysRole> findByUser(Integer userId, Pageable pageable);
}
