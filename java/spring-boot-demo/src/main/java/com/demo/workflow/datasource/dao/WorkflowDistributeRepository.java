package com.demo.workflow.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.workflow.datasource.entity.WorkflowDistribute;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowDistributeRepository extends CustomerBaseRepository<WorkflowDistribute> {


    void deleteByWorkflowActiveId(Integer workflowActiveId);

    WorkflowDistribute findByWorkflowActiveIdAndUserId(Integer workflowActiveId, Integer id);

    void deleteByWorkflowActiveIdAndUserId(Integer workflowActiveId, Integer id);
}
