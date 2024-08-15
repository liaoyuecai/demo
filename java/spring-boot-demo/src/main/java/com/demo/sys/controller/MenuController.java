package com.demo.sys.controller;

import com.demo.core.aop.RequestBaseEntitySet;
import com.demo.core.authentication.WebSecurityConfig;
import com.demo.core.dto.ApiHttpRequest;
import com.demo.core.dto.ApiHttpResponse;
import com.demo.core.dto.DeleteRequest;
import com.demo.sys.datasource.AuthUserCache;
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


    @PostMapping("/save")
    @RequestBaseEntitySet
    public ApiHttpResponse save(@RequestBody ApiHttpRequest<SysMenu> request) {
        menuService.save(request);
        return request.success();
    }

    @PostMapping("/page")
    public ApiHttpResponse<List<SysMenu>> page(@RequestBody MenuPageRequest request) {
        return request.success(menuService.findList(request.toExample()));
    }


    @PostMapping("/own")
    public ApiHttpResponse<List<SysMenu>> own(@RequestBody ApiHttpRequest request) {
        return request.success(menuService.findOwnList((AuthUserCache) request.getUser()));
    }

    @PostMapping("/delete")
    @RequestBaseEntitySet
    public ApiHttpResponse delete(@RequestBody DeleteRequest request) {
        menuService.deleteUpdate(request);
        return request.success();
    }
}
