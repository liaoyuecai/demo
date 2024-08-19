package com.demo.sys.controller;

import com.demo.core.authentication.WebSecurityConfig;
import com.demo.core.dto.ApiHttpRequest;
import com.demo.core.dto.ApiHttpResponse;
import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.dto.ResetRootPassword;
import com.demo.sys.service.AuthUserService;
import com.google.code.kaptcha.Producer;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 认证/用户相关
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private AuthUserService authUserService;

    @PostMapping("/resetRootPassword")
    public ApiHttpResponse<AuthUserCache> resetRootPassword(@RequestBody ResetRootPassword resetRootPassword) {
        authUserService.resetRootPassword(resetRootPassword);
        return ApiHttpResponse.success(SecurityContextHolder.getContext().getAuthentication().getDetails());
    }


}
