package com.demo.workflow.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.workflow.datasource.entity.WorkflowActiveHistory;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowActiveHistoryRepository extends CustomerBaseRepository<WorkflowActiveHistory> {


    WorkflowActiveHistory findByNodeIdAndStatus(Integer nodeId, int i);
}
