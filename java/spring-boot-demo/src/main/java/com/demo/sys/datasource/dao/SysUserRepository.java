package com.demo.sys.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.sys.datasource.entity.SysUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserRepository extends CustomerBaseRepository<SysUser> {


    SysUser findByUsernameAndStatusAndDeleted(String username, int i, int i1);

    SysUser findByUsernameAndDeleted(String username, int i);
}
