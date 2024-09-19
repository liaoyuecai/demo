package com.demo.workflow.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.workflow.datasource.entity.WorkflowNodeUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowNodeUserRepository extends CustomerBaseRepository<WorkflowNodeUser> {

    List<WorkflowNodeUser> findByNodeIdIn(List<Integer> allStartNodeIds);
}
