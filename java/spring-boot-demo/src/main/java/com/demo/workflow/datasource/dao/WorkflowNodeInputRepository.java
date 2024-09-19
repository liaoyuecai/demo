package com.demo.workflow.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.workflow.datasource.entity.WorkflowNodeInput;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowNodeInputRepository extends CustomerBaseRepository<WorkflowNodeInput> {

    List<WorkflowNodeInput> findByNodeIdAndDeleted(Integer nodeId, int i);
}
