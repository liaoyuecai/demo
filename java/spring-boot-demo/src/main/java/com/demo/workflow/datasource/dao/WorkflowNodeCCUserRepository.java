package com.demo.workflow.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.workflow.datasource.entity.WorkflowNodeCCUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowNodeCCUserRepository extends CustomerBaseRepository<WorkflowNodeCCUser> {

    @Query("""
            SELECT nu.userId  FROM WorkflowNodeCCUser nu
            WHERE nu.nodeId = :id 
            """)
    List<Integer> findUserIdsByNodeId(Integer id);

}
