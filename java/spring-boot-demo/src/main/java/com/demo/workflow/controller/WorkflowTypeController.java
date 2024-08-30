package com.demo.workflow.controller;

import com.demo.core.aop.RequestBaseEntitySet;
import com.demo.core.aop.RequestSetType;
import com.demo.core.dto.ApiHttpRequest;
import com.demo.core.dto.ApiHttpResponse;
import com.demo.core.dto.DeleteRequest;
import com.demo.workflow.datasource.entity.WorkflowType;
import com.demo.workflow.service.WorkflowTypeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/workflow/type")
public class WorkflowTypeController {
    @Resource
    private WorkflowTypeService service;


    @PostMapping("/save")
    @RequestBaseEntitySet(checkCreateBy = true, status = 1)
    public ApiHttpResponse save(@RequestBody ApiHttpRequest<WorkflowType> request) {
        service.save(request);
        return request.success();
    }


    @PostMapping("/list")
    public ApiHttpResponse<List<WorkflowType>> page(@RequestBody ApiHttpRequest request) {
        return request.success(service.findNotDeleted());
    }

    @PostMapping("/delete")
    @RequestBaseEntitySet(checkCreateBy = true, type = RequestSetType.DELETE)
    public ApiHttpResponse delete(@RequestBody DeleteRequest request) {
        service.deleteUpdate(request);
        return request.success();
    }
}
