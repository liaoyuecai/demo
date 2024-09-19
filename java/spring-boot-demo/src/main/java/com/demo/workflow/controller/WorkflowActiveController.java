package com.demo.workflow.controller;

import com.demo.core.aop.RequestBaseEntitySet;
import com.demo.core.dto.ApiHttpRequest;
import com.demo.core.dto.ApiHttpResponse;
import com.demo.core.dto.WebTreeNode;
import com.demo.workflow.datasource.dto.SaveHistory;
import com.demo.workflow.datasource.dto.WorkflowInputAndData;
import com.demo.workflow.service.WorkflowActiveService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/workflow/active")
public class WorkflowActiveController {
    @Resource
    private WorkflowActiveService service;


    @PostMapping("/list")
    @RequestBaseEntitySet(checkCreateBy = true)
    public ApiHttpResponse<List<WebTreeNode>> list(@RequestBody ApiHttpRequest request) {
        return request.success(service.findUserWorkflowList(request));
    }

    @PostMapping("/saveHistory")
    public ApiHttpResponse saveHistory(@RequestBody ApiHttpRequest<SaveHistory> request) {
        service.saveHistory(request);
        return request.success();
    }

    @PostMapping("/submit")
    public ApiHttpResponse submit(@RequestBody ApiHttpRequest<SaveHistory> request) {
        service.submit(request);
        return request.success();
    }

    @PostMapping("/start")
    public ApiHttpResponse<WorkflowInputAndData> start(@RequestBody ApiHttpRequest<Integer> request) {
        return request.success(service.start(request));
    }

    @PostMapping("/handle")
    public ApiHttpResponse<WorkflowInputAndData> handle(@RequestBody ApiHttpRequest<Integer> request) {
        service.handle(request);
        return request.success();
    }


}
