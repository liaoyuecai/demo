package com.demo.core.authentication;


import com.google.code.kaptcha.Producer;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Security配置项
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    public final static String HTTP_HEADER_AUTHORIZATION = "Authorization";
    public final static String REQUEST_ATTRIBUTE_AUTHENTICATION = "authentication";
    public final static String REQUEST_ATTRIBUTE_USER_DETAILS = "userDetails";
    public final static String VERIFICATION_CODE_SESSION_KEY = "verification-code";

    @Autowired(required = false)
    TokenManager tokenManager;
    @Autowired(required = false)
    UserDatasourceService userDatasourceService;
    @Autowired(required = false)
    PasswordAuthProvider authProvider;
    @Autowired(required = false)
    AuthenticationResultHandler resultHandler;
    @Autowired(required = false)
    AuthFilterConfig filterConfig;

    @Value("${authentication.permit:}")
    private List<String> permitList;
    @Value("${authentication.tokenTimeout:30}")
    private int tokenTimeout;
    @Value("${authentication.kaptchaUrl:/auth/captcha}")
    private String captchaUrl;

    @Autowired(required = false)
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    @Resource
    private Producer captchaProducer;


    /**
     * 检查参数实例化情况
     * <p>预留可以重写的处理器,如果没有重写就初始化</p>
     * <p>这里初始化的所有处理器都可以通过直接在spring注入一个继承类来重写其中函数</p>
     */
    @Autowired
    public void beanCheckAndInit() {
        if (tokenManager == null) tokenManager = new GoogleCacheTokenManager(tokenTimeout);
        if (passwordEncoder == null) passwordEncoder = new BCryptPasswordEncoder();
        if (userDatasourceService == null) userDatasourceService = new UserServiceDefaultImpl(passwordEncoder);
        if (authProvider == null)
            authProvider = new PasswordAuthProvider(userDatasourceService, passwordEncoder, tokenManager);
        if (resultHandler == null) {
            resultHandler = new AuthenticationResultHandler();
            resultHandler.setTokenManager(tokenManager);
        }
        if (filterConfig == null) filterConfig = new AuthFilterConfig();
        authenticationManager = new ProviderManager(Arrays.asList(authProvider));
    }


    /**
     * 核心配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorizeHttpRequests) -> {
                    authorizeHttpRequests.requestMatchers(captchaUrl).permitAll();
                    permitList.forEach(i -> authorizeHttpRequests.requestMatchers(i).permitAll());
                    authorizeHttpRequests.anyRequest().authenticated();
                }
        );
        // 禁用默认的formLogin
        http.formLogin((formLogin) -> formLogin.disable());
        // 禁用httpBasic
        http.httpBasic((httpBasic) -> httpBasic.disable());
        http.authenticationManager(authenticationManager);
        // 禁用csrf
        http.csrf((csrf) -> csrf.disable());
        // 将token解析认证信息的Filter 添加到SecurityContextHolderFilter之后
        http.addFilterAfter(new TokenSecurityContextHolderFilter(tokenManager, captchaUrl, captchaProducer), SecurityContextHolderFilter.class);
        // 启用自定义认证流程
        http.with(filterConfig, fl -> fl
                .loginSuccessHandler(resultHandler.loginSuccess)
                .loginFailHandler(resultHandler.loginFail));
        //配置注销
        http.logout((logout) -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessHandler(resultHandler.logoutSuccess));


        http.exceptionHandling((exception) -> exception
                .authenticationEntryPoint(resultHandler.authenticationEntry)
                .accessDeniedHandler(resultHandler.accessDenied)
        );
        return http.build();
    }


    /**
     * 配置自定义登录验证类
     * 此项配置会造成一些默认的Security bean失效（主要目的）
     *
     * @return
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return authenticationManager;
    }

}