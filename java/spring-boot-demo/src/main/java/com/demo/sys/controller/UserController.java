package com.demo.sys.controller;

import com.demo.core.aop.RequestBaseEntitySet;
import com.demo.core.aop.RequestSetType;
import com.demo.core.authentication.WebSecurityConfig;
import com.demo.core.dto.*;
import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.dto.CurrentUser;
import com.demo.sys.datasource.dto.ResetPassword;
import com.demo.sys.datasource.dto.SysUserDto;
import com.demo.sys.datasource.dto.UserBindJobAndRole;
import com.demo.sys.datasource.dto.page.UserPageRequest;
import com.demo.sys.datasource.entity.SysRole;
import com.demo.sys.datasource.entity.SysUser;
import com.demo.sys.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService service;


    @PostMapping("/save")
    @RequestBaseEntitySet(checkCreateBy = true)
    public ApiHttpResponse save(@RequestBody ApiHttpRequest<SysUser> request) {
        service.save(request);
        return request.success();
    }

    @PostMapping("/page")
    public ApiHttpResponse<PageList<SysUserDto>> page(@RequestBody UserPageRequest request) {
        return request.success(service.findPageCustomCriteria(request, SysUserDto.class));
    }

    @PostMapping("/findDeptAndJobs")
    public ApiHttpResponse<List<WebTreeNode>> findDeptAndJobs(@RequestBody ApiHttpRequest request) {
        return request.success(service.findDeptAndJobs());
    }

    @PostMapping("/findRoles")
    public ApiHttpResponse<List<SysRole>> findRoles(@RequestAttribute(WebSecurityConfig.REQUEST_ATTRIBUTE_USER_DETAILS) AuthUserCache cache) {
        return ApiHttpResponse.success(service.findRoles(cache));
    }

    @PostMapping("/updateSelf")
    public ApiHttpResponse updateSelf(@RequestBody ApiHttpRequest<SysUser> request, @RequestAttribute(WebSecurityConfig.REQUEST_ATTRIBUTE_AUTHENTICATION) Authentication authentication) {
        service.updateSelf(request, authentication);
        return request.success();
    }

    @PostMapping("/delete")
    @RequestBaseEntitySet(checkCreateBy = true, type = RequestSetType.DELETE)
    public ApiHttpResponse delete(@RequestBody DeleteRequest request) {
        service.deleteUpdate(request);
        return request.success();
    }

    @PostMapping("/bindJobAndRole")
    public ApiHttpResponse bindJobAndRole(@RequestBody ApiHttpRequest<UserBindJobAndRole> request) {
        service.bindJobAndRole(request);
        return request.success();
    }

    @PostMapping("/findBindJobAndRole")
    public ApiHttpResponse<UserBindJobAndRole> findBindJobAndRole(@RequestBody ApiHttpRequest<Integer> request) {
        return request.success(service.findBindJobAndRole(request.getData()));
    }

    @PostMapping("/current")
    public ApiHttpResponse<CurrentUser> current(@RequestAttribute(WebSecurityConfig.REQUEST_ATTRIBUTE_USER_DETAILS) AuthUserCache cache) {
        return ApiHttpResponse.success(service.currentUser(cache));
    }


    @PostMapping("/uploadAvatar")
    public ApiHttpResponse<String> uploadAvatar(@RequestParam("file") MultipartFile file, @RequestAttribute(WebSecurityConfig.REQUEST_ATTRIBUTE_AUTHENTICATION) Authentication authentication) throws IOException {
        return ApiHttpResponse.success(service.uploadAvatar(file, authentication));
    }

    @PostMapping("/resetPassword")
    public ApiHttpResponse<AuthUserCache> resetPassword(@RequestBody ApiHttpRequest<ResetPassword> request, @RequestAttribute(WebSecurityConfig.REQUEST_ATTRIBUTE_USER_DETAILS) AuthUserCache userDetails) {
        service.resetPassword(request.getData(), userDetails);
        return ApiHttpResponse.success(SecurityContextHolder.getContext().getAuthentication().getDetails());
    }
}
