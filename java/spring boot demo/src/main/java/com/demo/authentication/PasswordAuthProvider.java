package com.demo.authentication;


import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码校验
 * 并创建token
 */
public class PasswordAuthProvider implements AuthenticationProvider {

    protected final UserDetailsService userDetailsService;

    protected final PasswordEncoder passwordEncoder;

    protected final TokenManager tokenManager;


    // 构造方法
    public PasswordAuthProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, TokenManager tokenManager) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.tokenManager = tokenManager;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取未认证的token
        UserAuthenticationToken authenticationToken = (UserAuthenticationToken) authentication;
        String username = authenticationToken.getPrincipal();// 获取凭证也就是用户的手机号
        String password = authenticationToken.getCredentials(); // 获取输入的验证码
        // 获取用户信息
        AuthenticationUser user = (AuthenticationUser) userDetailsService.loadUserByUsername(username);
        // 验证密码是否匹配
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误");
        }

        UserAuthenticationToken authenticated = UserAuthenticationToken.createToken(user);
        user.setToken(tokenManager.generateToken(authenticated));
        return authenticated;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UserAuthenticationToken.class.isAssignableFrom(authentication);
    }
}