package com.demo.sys.service;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.core.service.impl.DefaultCURDService;
import com.demo.sys.datasource.entity.SysRole;
import com.demo.sys.datasource.entity.SysUser;
import org.springframework.stereotype.Service;

@Service("roleService")
public class RoleService extends DefaultCURDService<SysRole> {

    public RoleService(CustomerBaseRepository<SysRole> repository) {
        super(repository);
    }
}
