package com.demo.sys.datasource.dao;


import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.sys.datasource.entity.SysDepartment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysDepartmentRepository extends CustomerBaseRepository<SysDepartment> {


    @Query("""
            SELECT d FROM SysDepartment d JOIN SysUserDepartment ud ON
            ud.departmentId = d.id
             WHERE ud.userId = :userId and d.status = 1 and d.deleted !=1
            """)
    List<SysDepartment> findByUserId(Integer userId);

    @Query("""
            SELECT d.id FROM SysDepartment d JOIN SysUserDepartment ud ON
            ud.departmentId = d.id
             WHERE ud.userId = :userId and d.status = 1 and d.deleted !=1
            """)
    List<Integer> findIdByUserId(Integer userId);


}
