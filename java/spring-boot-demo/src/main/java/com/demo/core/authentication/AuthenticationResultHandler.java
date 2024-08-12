package com.demo.core.authentication;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * 结果处理器
 */
@Getter
@Slf4j
public class AuthenticationResultHandler {
    /**
     * 等录成功
     */
    protected final AuthenticationSuccessHandler loginSuccess;
    /**
     * 登录失败
     */
    protected final AuthenticationFailureHandler loginFail;
    /**
     * 注销
     */
    protected final LogoutSuccessHandler logoutSuccess;
    /**
     * 用户未认证异常处理
     */
    protected final AuthenticationEntryPoint authenticationEntry;
    /**
     * 鉴权失败异常处理
     */
    protected final AccessDeniedHandler accessDenied;
    private final ObjectMapper objectMapper = new ObjectMapper();

    protected TokenManager tokenManager;

    public void setTokenManager(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }


    public AuthenticationResultHandler() {
        this.loginSuccess = (_, response, authentication) -> {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpStatus.OK.value());
            AuthenticationResponse authenticationResponse;
            if (authentication instanceof UserAuthenticationToken)
                authenticationResponse = AuthenticationResponse.loginSuccess(
                        (AuthenticationUser) authentication.getDetails());
            else
                authenticationResponse = AuthenticationResponse.loginError("login filter authentication type error");
            response.getWriter().write(objectMapper.writeValueAsString(authenticationResponse));
        };
        this.loginFail = (_, response, exception) -> {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().write(objectMapper.writeValueAsString(AuthenticationResponse.loginError(exception.getMessage())));
        };
        this.logoutSuccess = (_, response, authentication) -> {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpStatus.OK.value());
            if (authentication instanceof UserAuthenticationToken) {
                if (tokenManager != null)
                    tokenManager.removeToken(
                            ((AuthenticationUser)
                                    authentication.getDetails()).getToken());
                response.getWriter().write(objectMapper.writeValueAsString(AuthenticationResponse.success()));
            }
        };
        this.authenticationEntry = (_, response, authException) -> {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().write(objectMapper.writeValueAsString(AuthenticationResponse.loginError(authException.getMessage())));
        };
        this.accessDenied = (_, response, accessDeniedException) -> {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().write(objectMapper.writeValueAsString(AuthenticationResponse.tokenError("权限异常:" + accessDeniedException.getMessage())));
        };
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


}
