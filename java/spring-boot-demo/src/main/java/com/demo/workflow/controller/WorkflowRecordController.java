package com.demo.workflow.controller;

import com.demo.core.aop.RequestBaseEntitySet;
import com.demo.core.aop.RequestSetType;
import com.demo.core.dto.ApiHttpRequest;
import com.demo.core.dto.ApiHttpResponse;
import com.demo.core.dto.DeleteRequest;
import com.demo.core.dto.PageList;
import com.demo.sys.datasource.dto.SimpleUserDto;
import com.demo.workflow.datasource.dto.page.WorkflowRecordRequest;
import com.demo.workflow.datasource.entity.WorkflowRecord;
import com.demo.workflow.service.WorkflowRecordService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/workflow/record")
public class WorkflowRecordController {
    @Resource
    private WorkflowRecordService service;


    @PostMapping("/save")
    @RequestBaseEntitySet(checkCreateBy = true)
    public ApiHttpResponse save(@RequestBody ApiHttpRequest<WorkflowRecord> request) {
        service.save(request);
        return request.success();
    }

    @PostMapping("/users")
    public ApiHttpResponse<List<SimpleUserDto>> users(@RequestBody ApiHttpRequest request) {
        return request.success(service.findUsers());
    }

    @PostMapping("/page")
    public ApiHttpResponse<PageList<WorkflowRecord>> page(@RequestBody WorkflowRecordRequest request) {
        return request.success(service.findPage(request));
    }


    @PostMapping("/delete")
    @RequestBaseEntitySet(checkCreateBy = true, type = RequestSetType.DELETE)
    public ApiHttpResponse delete(@RequestBody DeleteRequest request) {
        service.deleteUpdate(request);
        return request.success();
    }
}
