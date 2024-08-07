package com.demo.sys.controller;

import com.demo.core.aop.RequestSave;
import com.demo.core.aop.RequestSelect;
import com.demo.core.dto.ApiHttpRequest;
import com.demo.core.dto.ApiHttpResponse;
import com.demo.core.dto.DeleteRequest;
import com.demo.sys.datasource.AuthUserCache;
import com.demo.sys.datasource.dto.page.MenuPageRequest;
import com.demo.sys.datasource.entity.SysMenu;
import com.demo.sys.service.MenuService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {
    @Resource
    private MenuService menuService;


    @PostMapping("/save")
    @RequestSave
    public ApiHttpResponse<AuthUserCache> save(@RequestBody ApiHttpRequest<SysMenu> request) {
        menuService.insert(request.getData());
        return request.success();
    }

    @PostMapping("/page")
    @RequestSelect
    public ApiHttpResponse<List<SysMenu>> page(@RequestBody MenuPageRequest request) {
        return request.success(menuService.findList(request.toExample()));
    }

    @PostMapping("/delete")
    public ApiHttpResponse<AuthUserCache> delete(@RequestBody DeleteRequest request) {
        menuService.deleteUpdate(request.getData());
        return request.success();
    }
}
