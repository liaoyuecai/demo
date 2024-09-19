package com.demo.workflow.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.workflow.datasource.entity.WorkflowActive;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowActiveRepository extends CustomerBaseRepository<WorkflowActive> {


    WorkflowActive findByWorkflowIdAndNodeId(Integer workflowId, Integer nodeId1);
    @Modifying
    @Query("""
            update WorkflowActive wa set wa.updateBy = :userId 
            where wa.workflowId = :workflowId and wa.updateBy is null
            """)
    int updateUpdateBy(Integer userId, Integer workflowId);
}
