package com.demo.sys.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.sys.datasource.entity.SysUserJob;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SysUserJobRepository extends CustomerBaseRepository<SysUserJob> {
    void deleteByUserId(Integer userId);

    void deleteByUserIdIn(List<Integer> ids);

    List<SysUserJob> findByUserIdIn(Collection<Integer> userIds);

    List<SysUserJob> findByUserId(Integer userId);


    int deleteByJobIdIn(Collection<Integer> jobs);

}
