package com.demo.workflow.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.workflow.datasource.entity.WorkflowNodeJob;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowNodeJobRepository extends CustomerBaseRepository<WorkflowNodeJob> {

    List<WorkflowNodeJob> findByNodeIdIn(List<Integer> allStartNodeIds);
}
