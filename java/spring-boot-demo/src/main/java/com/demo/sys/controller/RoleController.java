package com.demo.sys.controller;

import com.demo.core.aop.RequestSave;
import com.demo.core.aop.RequestSelect;
import com.demo.core.authentication.WebSecurityConfig;
import com.demo.core.dto.ApiHttpRequest;
import com.demo.core.dto.ApiHttpResponse;
import com.demo.core.dto.DeleteRequest;
import com.demo.core.dto.PageList;
import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.dto.RoleBindMenu;
import com.demo.sys.datasource.dto.SysRoleDto;
import com.demo.sys.datasource.dto.page.RolePageRequest;
import com.demo.sys.datasource.entity.SysRole;
import com.demo.sys.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Resource
    private RoleService service;


    @PostMapping("/save")
    @RequestSave
    public ApiHttpResponse save(@RequestBody ApiHttpRequest<SysRole> request) {
        service.save(request.getData());
        return request.success();
    }

    @PostMapping("/page")
    @RequestSelect
    public ApiHttpResponse<PageList<SysRoleDto>> page(@RequestBody RolePageRequest request) {
        return request.success(service.findPageCustomCriteria(request, SysRoleDto.class));
    }

    @PostMapping("/delete")
    public ApiHttpResponse delete(@RequestBody DeleteRequest request) {
        service.deleteUpdate(request.getData());
        return request.success();
    }

    @PostMapping("/bindMenu")
    public ApiHttpResponse bindMenu(@RequestBody ApiHttpRequest<RoleBindMenu> request,
                                    @RequestAttribute(WebSecurityConfig.REQUEST_ATTRIBUTE_USER_DETAILS) AuthUserCache userDetails) {
        service.bindMenu(request.getData(), userDetails);
        return request.success();
    }

    @PostMapping("/findBindMenus")
    public ApiHttpResponse<List<Integer>> findBindMenus(@RequestBody ApiHttpRequest<Integer> request) {
        return request.success(service.findBindMenusId(request.getData()));
    }


}
