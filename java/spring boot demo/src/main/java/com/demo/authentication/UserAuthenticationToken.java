package com.demo.authentication;


import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 自定义token实体
 */
public class UserAuthenticationToken extends AbstractAuthenticationToken {

    private String username;
    private String password;

    private UserAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    // 未认证时
    public static UserAuthenticationToken getInstance(String username, String password) {
        UserAuthenticationToken token = new UserAuthenticationToken(null);
        token.username = username;
        token.password = password;
        token.setAuthenticated(Boolean.FALSE);
        return token;
    }

    // 认证成功后
    public static UserAuthenticationToken createToken(AuthenticationUser user) {
        UserAuthenticationToken authenticationToken = new UserAuthenticationToken(user.getAuthorities());
        authenticationToken.username = user.getUsername();
        authenticationToken.password = user.getPassword();
        // 认证成功后，将密码清除
        user.setPassword(null);
        // 设置为已认证状态
        authenticationToken.setAuthenticated(Boolean.TRUE);
        // 用户详情为MyUser
        authenticationToken.setDetails(user);
        return authenticationToken;
    }

    @Override
    public String getCredentials() {
        return this.password;
    }

    @Override
    public String getPrincipal() {
        return this.username;
    }


}
