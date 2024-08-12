package com.demo.sys.service;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.core.service.CURDService;
import com.demo.sys.datasource.dao.SysPermissionRepository;
import com.demo.sys.datasource.entity.SysPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("permissionService")
public class PermissionService extends CURDService<SysPermission, SysPermissionRepository> {

    public PermissionService(@Autowired SysPermissionRepository repository) {
        super(repository);
    }
}
