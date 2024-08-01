package com.demo.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * 自定义认证方式的Config类，可以参考SpringSecurity提供的默认form表单认证的配置类 FormLoginConfigurer
 * <p>
 * 主要的功能：
 * 1. 添加Token认证的AuthenticationProvider
 * 2. 创建自定义的Filter，并未filter设置SecurityContextRepository和SecurityContextHolderStrategy
 * 3. 将filter添加到过滤器链中
 * 4. (可选)定义Filter的部分可自配置的选项 eg：获取凭证的key，匹配认证URL的RequestMatcher，认证后的Handler 等
 */
public class AuthFilterConfig<H extends HttpSecurityBuilder<H>> extends AbstractAuthenticationFilterConfigurer<H, AuthFilterConfig<H>, ConvertJsonFilter> {


    /**
     * 登录路由默认了，需要改直接重写此类
     */
    public AuthFilterConfig() {
        super(new ConvertJsonFilter("/auth/login", "POST"), "/auth/login");
    }


    @Override
    public void init(H http) throws Exception {
        super.init(http);
    }

    // 主要的配置方法
    @Override
    public void configure(H http) throws Exception {
        // 添加认证提供者
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        // 设置过滤器 获取前端json数据并转换为security要求的格式
        ConvertJsonFilter jsonFilter = this.getAuthenticationFilter();
        jsonFilter.setAuthenticationManager(authenticationManager);

        // 将filter添加到过滤器链中 SecurityFilterChain，放在登录校验之前
        http.addFilterAfter(jsonFilter, UsernamePasswordAuthenticationFilter.class);
    }


    // 设置登录页面地址
    public AuthFilterConfig<H> loginPage(String loginPage) {
        return super.loginPage(loginPage);
    }

    // 设置认证URL
    @Override
    public AuthFilterConfig<H> loginProcessingUrl(String loginProcessingUrl) {
        return super.loginProcessingUrl(loginProcessingUrl);
    }

    public AuthFilterConfig<H> loginSuccessHandler(AuthenticationSuccessHandler handler) {
        (this.getAuthenticationFilter()).setAuthenticationSuccessHandler(handler);
        return this;
    }

    public AuthFilterConfig<H> loginFailHandler(AuthenticationFailureHandler handler) {
        (this.getAuthenticationFilter()).setAuthenticationFailureHandler(handler);
        return this;
    }

    // 配置认证URL的RequestMatcher
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }


}