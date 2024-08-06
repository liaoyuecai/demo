package com.demo.sys.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.sys.datasource.entity.SysUserRole;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SysUserRoleRepository extends CustomerBaseRepository<SysUserRole> {
    long deleteByUserIdIn(Collection<Integer> userIds);

    void deleteByUserId(Integer id);

    List<SysUserRole> findByUserIdIn(Collection<Integer> userIds);

    List<SysUserRole> findByUserId(Integer userId);

    List<Integer> findRoleIdByUserId(Integer id);

    int deleteByRoleIdIn(Collection<Integer> roles);
}
