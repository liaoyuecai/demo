package com.demo.authentication;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用户信息
 * 用于保存其他需要保存的用户信息
 * 有更复杂需求时继承此类并重写验证类PasswordAuthProvider
 */
@Setter
@Getter
public class AuthenticationUser implements UserDetails {
    private String username;
    private String password;

    private String token;

    private List<GrantedAuthority> authorities = new ArrayList<>();

    public AuthenticationUser() {
    }

    public AuthenticationUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


}
