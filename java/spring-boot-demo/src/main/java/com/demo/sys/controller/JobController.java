package com.demo.sys.controller;

import com.demo.core.aop.RequestSave;
import com.demo.core.aop.RequestSelect;
import com.demo.core.dto.ApiHttpRequest;
import com.demo.core.dto.ApiHttpResponse;
import com.demo.core.dto.DeleteRequest;
import com.demo.sys.datasource.dto.page.JobPageRequest;
import com.demo.sys.datasource.entity.SysJob;
import com.demo.sys.service.JobService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/job")
public class JobController {
    @Resource
    private JobService jobService;


    @PostMapping("/save")
    @RequestSave
    public ApiHttpResponse save(@RequestBody ApiHttpRequest<SysJob> request) {
        jobService.save(request.getData());
        return request.success();
    }

    @PostMapping("/page")
    @RequestSelect
    public ApiHttpResponse<List<SysJob>> page(@RequestBody JobPageRequest request) {
        if (request.getData() != null && request.getData().getDeptId() == null)
            request.getData().setDeptId(-1);
        return request.success(jobService.findList(request.toExample()));
    }


    @PostMapping("/delete")
    public ApiHttpResponse delete(@RequestBody DeleteRequest request) {
        jobService.deleteUpdate(request.getData());
        return request.success();
    }
}
