package com.demo.workflow.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.workflow.datasource.entity.WorkflowNodeInput;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowNodeInputRepository extends CustomerBaseRepository<WorkflowNodeInput> {

}
