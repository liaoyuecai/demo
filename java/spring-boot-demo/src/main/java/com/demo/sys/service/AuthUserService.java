package com.demo.sys.service;

import com.demo.core.authentication.UserDatasourceService;
import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.dao.SysRoleRepository;
import com.demo.sys.datasource.dao.SysUserRepository;
import com.demo.sys.datasource.entity.SysUser;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthUserService implements UserDatasourceService {
    @Resource
    SysUserRepository userRepository;
    @Resource
    SysRoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userRepository.findByUsernameAndStatusAndDeleted(username, 1, 0);
        if (user == null) return null;
        AuthUserCache userCache = new AuthUserCache(user);
        if ("admin".equals(user.getUsername())) userCache.setRoot(true);
        return userCache;
    }

    @Override
    public void loadUserAuthority(UserDetails details) {
        AuthUserCache userCache = (AuthUserCache) details;
        //todo 实际不需要这么多字段，后面修改
        if (userCache.isRoot())
            userCache.setRoleList(roleRepository.findNotDeletedAndStatus());
        else
            userCache.setRoleList(roleRepository.findRolesByUserId(userCache.getId()));
    }
}
