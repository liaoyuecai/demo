package com.demo.sys.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.sys.datasource.entity.SysRolePermission;
import org.springframework.stereotype.Repository;

@Repository
public interface SysRolePermissionRepository extends CustomerBaseRepository<SysRolePermission>{
}