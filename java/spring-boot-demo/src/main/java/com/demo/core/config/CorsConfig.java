package com.demo.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 你需要跨域的地址,* 表示对所有的地址都可以访问
        corsConfiguration.addAllowedOrigin("*");
        // 跨域的请求头
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST"));
        // 跨域的请求方法
        corsConfiguration.addAllowedMethod("*");
        // 可以携带 cookie，可以 在跨域请求的时候获取同一个 session
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 配置 可以访问的地址
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

}