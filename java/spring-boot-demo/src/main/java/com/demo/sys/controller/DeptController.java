package com.demo.sys.controller;

import com.demo.core.aop.RequestBaseEntitySet;
import com.demo.core.dto.ApiHttpRequest;
import com.demo.core.dto.ApiHttpResponse;
import com.demo.core.dto.DeleteRequest;
import com.demo.sys.datasource.dto.page.DeptPageRequest;
import com.demo.sys.datasource.entity.SysDepartment;
import com.demo.sys.service.DepartmentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dept")
public class DeptController {
    @Resource
    private DepartmentService service;


    @PostMapping("/save")
    @RequestBaseEntitySet
    public ApiHttpResponse save(@RequestBody ApiHttpRequest<SysDepartment> request) {
        service.save(request);
        return request.success();
    }

    @PostMapping("/page")
    public ApiHttpResponse<List<SysDepartment>> page(@RequestBody DeptPageRequest request) {
        return request.success(service.findList(request.toExample()));
    }


    @PostMapping("/own")
    public ApiHttpResponse<List<SysDepartment>> own(@RequestBody ApiHttpRequest request) {
        return request.success(service.findNotDeletedAndStatus());
    }


    @PostMapping("/delete")
    @RequestBaseEntitySet
    public ApiHttpResponse delete(@RequestBody DeleteRequest request) {
        service.deleteUpdate(request);
        return request.success();
    }
}
