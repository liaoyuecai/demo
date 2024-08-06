package com.demo.sys.controller;

import com.demo.core.authentication.WebSecurityConfig;
import com.demo.core.dto.ApiHttpRequest;
import com.demo.core.dto.ApiHttpResponse;
import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.dto.ResetPassword;
import com.demo.sys.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    public ApiHttpResponse<AuthUserCache> resetPassword(@RequestBody ApiHttpRequest<ResetPassword> request, @RequestAttribute(WebSecurityConfig.REQUEST_ATTRIBUTE_USER_DETAILS) AuthUserCache userDetails) {
        userService.resetPassword(request.getData(), userDetails);
        return ApiHttpResponse.success(SecurityContextHolder.getContext().getAuthentication().getDetails());
    }
}
