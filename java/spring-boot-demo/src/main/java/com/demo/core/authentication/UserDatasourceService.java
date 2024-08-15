package com.demo.core.authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserDatasourceService extends UserDetailsService {
    /**
     * 验证成功后加载权限数据，如菜单，角色
     * @param details
     */
    default void loadUserAuthority(UserDetails details){

    }
}
