package com.demo.core.authentication;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

/**
 * token验证
 */
@Getter
@Setter
public class TokenSecurityContextHolderFilter extends GenericFilterBean {

    private TokenManager tokenManager;

    public TokenSecurityContextHolderFilter(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 从请求头中获取Token
        String reqToken = httpRequest.getHeader(WebSecurityConfig.HTTP_HEADER_AUTHORIZATION);

        // 如果没有Token或Token为空，则继续过滤器链
        if (StringUtils.isBlank(reqToken)) {
            chain.doFilter(request, response);
            return;
        }

        // 尝试通过Token获取Authentication对象
        Authentication authentication = tokenManager.getAuthenticationByToken(reqToken);
        if (authentication == null || !checkAuthentication(request, authentication)) {
            chain.doFilter(request, response);
            return;
        } else {
            //有效token，为其续期
            tokenManager.delayExpired(reqToken);
        }

        request.setAttribute(WebSecurityConfig.REQUEST_ATTRIBUTE_AUTHENTICATION, authentication);
        request.setAttribute(WebSecurityConfig.REQUEST_ATTRIBUTE_USER_DETAILS, authentication.getDetails());
        // 设置SecurityContextHolder中的Authentication对象

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    /**
     * 详细校验权限
     * 预留给重写
     *
     * @param request
     * @param authentication
     * @return
     */
    protected boolean checkAuthentication(ServletRequest request, Authentication authentication) {
        return true;
    }

}