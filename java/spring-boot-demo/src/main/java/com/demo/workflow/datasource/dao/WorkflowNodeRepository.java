package com.demo.workflow.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.workflow.datasource.entity.WorkflowNode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowNodeRepository extends CustomerBaseRepository<WorkflowNode> {


    @Modifying
    @Query("""
            update WorkflowNode wn set wn.deleted = 1 where wn.workflowId = :id
            """)
    void updateDeletedByWorkflowId(Integer id);
}
