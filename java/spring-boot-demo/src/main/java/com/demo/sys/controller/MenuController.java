package com.demo.sys.controller;

import com.demo.core.aop.RequestSave;
import com.demo.core.aop.RequestSelect;
import com.demo.core.authentication.WebSecurityConfig;
import com.demo.core.dto.ApiHttpRequest;
import com.demo.core.dto.ApiHttpResponse;
import com.demo.core.dto.DeleteRequest;
import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.dao.SysUserRepository;
import com.demo.sys.datasource.dto.page.MenuPageRequest;
import com.demo.sys.datasource.entity.SysMenu;
import com.demo.sys.service.MenuService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {
    @Resource
    private MenuService menuService;

    @Resource
    SysUserRepository repository;


    @PostMapping("/save")
    @RequestSave
    public ApiHttpResponse save(@RequestBody ApiHttpRequest<SysMenu> request) {
        menuService.insert(request.getData());
        return request.success();
    }

    @PostMapping("/page")
    @RequestSelect
    public ApiHttpResponse<List<SysMenu>> page(@RequestBody MenuPageRequest request) {
        return request.success(menuService.findList(request.toExample()));
    }


    @PostMapping("/own")
    public ApiHttpResponse<List<SysMenu>> own(@RequestBody ApiHttpRequest request,
                                              @RequestAttribute(WebSecurityConfig.REQUEST_ATTRIBUTE_USER_DETAILS) AuthUserCache userDetails) {
        return request.success(menuService.findOwnList(userDetails));
    }

    @PostMapping("/delete")
    public ApiHttpResponse delete(@RequestBody DeleteRequest request) {
        menuService.deleteUpdate(request.getData());
        return request.success();
    }
}
