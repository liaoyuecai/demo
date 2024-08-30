package com.demo.workflow.service;

import com.demo.core.service.CURDService;
import com.demo.workflow.datasource.dao.WorkflowTypeRepository;
import com.demo.workflow.datasource.entity.WorkflowType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("workflowTypeService")
public class WorkflowTypeService extends CURDService<WorkflowType, WorkflowTypeRepository> {


    public WorkflowTypeService(@Autowired WorkflowTypeRepository repository) {
        super(repository);
    }


}
