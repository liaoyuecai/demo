package com.demo.core.authentication;

import org.springframework.security.core.Authentication;

/**
 * token 管理器
 */
public interface TokenManager {


    String generateToken(Authentication authentication);


    void removeToken(String token);


    Authentication getAuthenticationByToken(String token);


    void delayExpired(String token);
}
