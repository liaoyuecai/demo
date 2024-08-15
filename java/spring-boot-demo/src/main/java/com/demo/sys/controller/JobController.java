package com.demo.sys.controller;

import com.demo.core.aop.RequestBaseEntitySet;
import com.demo.core.aop.RequestSetType;
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
    @RequestBaseEntitySet(checkCreateBy = true)
    public ApiHttpResponse save(@RequestBody ApiHttpRequest<SysJob> request) {
        jobService.save(request);
        return request.success();
    }

    @PostMapping("/page")
    public ApiHttpResponse<List<SysJob>> page(@RequestBody JobPageRequest request) {
        if (request.getData() != null && request.getData().getDeptId() == null)
            request.getData().setDeptId(-1);
        return request.success(jobService.findList(request.toExample()));
    }


    @PostMapping("/delete")
    @RequestBaseEntitySet(checkCreateBy = true,type = RequestSetType.DELETE)
    public ApiHttpResponse delete(@RequestBody DeleteRequest request) {
        jobService.deleteUpdate(request);
        return request.success();
    }
}
