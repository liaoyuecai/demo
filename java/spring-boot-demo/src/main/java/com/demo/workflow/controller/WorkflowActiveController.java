package com.demo.workflow.controller;

import com.demo.core.dto.*;
import com.demo.workflow.datasource.dto.SaveHistory;
import com.demo.workflow.datasource.dto.WorkflowActiveDto;
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


    @PostMapping("/createList")
    public ApiHttpResponse<List<WebTreeNode>> createList(@RequestBody ApiHttpRequest request) {
        return request.success(service.findUserWorkflowList(request));
    }

    @PostMapping("/page")
    public ApiHttpResponse<PageList<WorkflowActiveDto>> page(@RequestBody PageListRequest<WorkflowActiveDto> request) {
        return request.success(service.findDtoPage(request));
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
