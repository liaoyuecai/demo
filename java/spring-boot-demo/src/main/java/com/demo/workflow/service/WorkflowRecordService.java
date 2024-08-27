package com.demo.workflow.service;

import com.demo.core.service.CURDService;
import com.demo.sys.datasource.dao.SysUserRepository;
import com.demo.sys.datasource.dto.SimpleUserDto;
import com.demo.sys.datasource.dto.SysUserDto;
import com.demo.sys.datasource.entity.SysUser;
import com.demo.workflow.datasource.dao.WorkflowRecordRepository;
import com.demo.workflow.datasource.entity.WorkflowRecord;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("workflowRecordService")
public class WorkflowRecordService extends CURDService<WorkflowRecord, WorkflowRecordRepository> {

    @Resource
    SysUserRepository userRepository;

    public WorkflowRecordService(@Autowired WorkflowRecordRepository repository) {
        super(repository);
    }


    public List<SimpleUserDto> findUsers(){
        return userRepository.findSimpleUsersDto();
    }
}
