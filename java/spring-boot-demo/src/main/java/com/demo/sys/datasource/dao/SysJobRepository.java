package com.demo.sys.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.sys.datasource.entity.SysJob;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysJobRepository extends CustomerBaseRepository<SysJob> {

    @Query("""
            SELECT j FROM SysJob j JOIN SysUserJob uj ON
            uj.jobId = j.id
             WHERE uj.userId = :userId and j.status = 1 and j.deleted = 0
            """)
    List<SysJob> findByUserId(Integer userId);

    @Query("""
            SELECT j.deptId FROM SysJob j JOIN SysUserJob uj ON
            uj.jobId = j.id
             WHERE uj.userId = :userId and j.status = 1 and j.deleted = 0
            """)
    List<Integer> findDeptIdByUserId(Integer userId);
}
