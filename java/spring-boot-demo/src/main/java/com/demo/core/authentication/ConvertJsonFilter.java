package com.demo.core.authentication;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

/**
 * 自定义认证filter，主要用于识别json登录参数
 */
@Slf4j
public class ConvertJsonFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 配置登录url
     *
     * @param path
     * @param method
     */
    public ConvertJsonFilter(String path, String method) {
        super(new AntPathRequestMatcher(path, method));
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // 自定义RequestMatcher的构造方法
    public ConvertJsonFilter(AntPathRequestMatcher matcher) {
        super(matcher);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // 提供attemptAuthentication方法，该方法的主要作用是从Request中获取到用户名密码并组成未认证的AuthenticationToken
    // 组成未认证的AuthenticationToken后,调用AuthenticationManager(实际为ProviderManager)去认证AuthenticationToken
    // 认证成功后，返回已认证的AuthenticationToken 如果认证失败则在认证过程中抛出异常
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        request.setCharacterEncoding("UTF8");
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpStatus.OK.value());
        AuthenticationUser user = requestToJsonUser(request);
        if (StringUtils.isBlank(user.getUsername())) {
            throw new BadCredentialsException("用户名为空");
        }
        if (StringUtils.isBlank(user.getPassword())) {
            throw new BadCredentialsException("密码为空");
        }
        if (StringUtils.isBlank(user.getVerificationCode())) {
            throw new BadCredentialsException("验证码为空");
        }
        HttpSession session = request.getSession();
        if (!user.getVerificationCode().toLowerCase().equals(session.getAttribute(WebSecurityConfig.VERIFICATION_CODE_SESSION_KEY))){
            session.removeAttribute(WebSecurityConfig.VERIFICATION_CODE_SESSION_KEY);
            throw new BadCredentialsException("验证码错误");
        }else {
            session.removeAttribute(WebSecurityConfig.VERIFICATION_CODE_SESSION_KEY);
        }
        UserAuthenticationToken authRequest = UserAuthenticationToken.getInstance(user.getUsername(), user.getPassword());
        try {
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * 获取user
     * 方便重写
     *
     * @param request
     * @return
     * @throws IOException
     */
    protected AuthenticationUser requestToJsonUser(HttpServletRequest request) throws IOException {
        return objectMapper.readValue(request.getInputStream(), AuthenticationUser.class);
    }

}
