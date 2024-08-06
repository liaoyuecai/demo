package com.demo.sys.service;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.core.service.impl.DefaultCURDService;
import com.demo.sys.datasource.entity.SysPermission;
import com.demo.sys.datasource.entity.SysUser;
import org.springframework.stereotype.Service;

@Service("permissionService")
public class PermissionService extends DefaultCURDService<SysPermission> {

    public PermissionService(CustomerBaseRepository<SysPermission> repository) {
        super(repository);
    }
}
