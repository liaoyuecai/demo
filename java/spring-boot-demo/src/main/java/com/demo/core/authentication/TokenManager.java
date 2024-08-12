package com.demo.core.authentication;

import org.springframework.security.core.Authentication;

/**
 * token 管理器
 */
public interface TokenManager {


    /**
     * 初始化token
     * @param authentication
     * @return
     */
    String generateToken(Authentication authentication);


    void removeToken(String token);

    void refreshToken(String token,Authentication authentication);

    /**
     * 根据token获取验证信息
     * @param token
     * @return
     */
    Authentication getAuthenticationByToken(String token);


    /**
     * token 续期
     * @param token
     */
    void delayExpired(String token);

}
