package com.demo.workflow.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.workflow.datasource.entity.WorkflowDistribute;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowDistributeRepository extends CustomerBaseRepository<WorkflowDistribute> {


    void deleteByWorkflowId(Integer workflowId);

    WorkflowDistribute findByWorkflowIdAndUserId(Integer workflowId, Integer id);
}
