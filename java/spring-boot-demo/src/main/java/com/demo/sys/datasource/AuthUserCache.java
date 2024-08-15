package com.demo.sys.datasource;

import com.demo.core.authentication.AuthenticationUser;
import com.demo.sys.datasource.entity.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 登录后保存的用户信息
 */
@Getter
@Setter
public class AuthUserCache extends AuthenticationUser {
    //识别是否root账户

    private String avatar;
    private List<SysRole> roleList;
    private List<SysMenu> menuList;

    public AuthUserCache(SysUser user) {
        super(user.getUsername(), user.getPassword());
        this.id = user.getId();
        this.setUsername(user.getRealName());
        this.avatar = user.getUserAvatar();
    }
}
