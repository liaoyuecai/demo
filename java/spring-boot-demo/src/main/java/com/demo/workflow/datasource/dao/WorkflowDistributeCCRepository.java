package com.demo.workflow.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.workflow.datasource.entity.WorkflowDistributeCC;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowDistributeCCRepository extends CustomerBaseRepository<WorkflowDistributeCC> {


    void deleteByWorkflowId(Integer workflowId);
}
