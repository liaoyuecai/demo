package com.demo.sys.service;

import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.dao.SysRoleRepository;
import com.demo.sys.datasource.dao.SysUserRepository;
import com.demo.sys.datasource.entity.SysRole;
import com.demo.sys.datasource.entity.SysUser;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthUserService implements UserDetailsService {
    @Resource
    SysUserRepository userRepository;
    @Resource
    SysRoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userRepository.findByUsernameAndStatusAndDeleted(username, 1, 0);
        if (user == null) return null;
        AuthUserCache userCache = new AuthUserCache(user);
        List<SysRole> roles = roleRepository.findRolesByUserId(userCache.getId());
        if (roles != null) {
            //todo 实际不需要这么多字段，后面修改
            userCache.setRoleList(roles);
        }
        if ("admin".equals(user.getUsername())) userCache.setRoot(true);
        return userCache;
    }
}
