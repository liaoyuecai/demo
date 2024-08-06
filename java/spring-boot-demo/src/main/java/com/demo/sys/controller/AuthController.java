package com.demo.sys.controller;

import com.demo.core.authentication.WebSecurityConfig;
import com.demo.core.dto.ApiHttpResponse;
import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.service.UserService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证/用户相关
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private UserService userService;


    @PostMapping("/current")
    public ApiHttpResponse<AuthUserCache> current(@RequestAttribute(WebSecurityConfig.REQUEST_ATTRIBUTE_USER_DETAILS) AuthUserCache userDetails) {
        userService.currentUser(userDetails);
        return ApiHttpResponse.success(userDetails);
    }


    @PostMapping("/resetPassword")
    public ApiHttpResponse<AuthUserCache> resetPassword() {
        return ApiHttpResponse.success(SecurityContextHolder.getContext().getAuthentication().getDetails());
    }
}
