package com.demo.workflow.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.workflow.datasource.entity.WorkflowNode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowNodeRepository extends CustomerBaseRepository<WorkflowNode> {


    @Modifying
    @Query("""
            update WorkflowNode wn set wn.deleted = 1 where wn.workflowId = :id
            """)
    void updateDeletedByWorkflowId(Integer id);

    @Query("""
            select t from WorkflowNode t where t.deleted = 0 and t.nodeType = 1 
            """)
    List<WorkflowNode> findAddStartNodes();

    @Query("""
            select t from WorkflowNode t where t.deleted = 0 and t.nodeType = 1 
            and workflowId = :workflowId
            """)
    WorkflowNode findStartNode(Integer workflowId);

    @Query("""
            select t2 from WorkflowNode t1 JOIN WorkflowNode t2 
            ON t1.id = t2.parent_id
             where t1.deleted = 0 and t1.nodeType = 1 and t1.workflowId = :id
            """)
    WorkflowNode findWorkflowFirstNode(Integer id);

    @Query("""
            SELECT DISTINCT nu.userId  FROM workflowNodeUser nu
            WHERE nu.nodeId = :id 
            UNION DISTINCT
            SELECT DISTINCT su.userId
            FROM workflowNodeJob nj
            JOIN sysUserJob suj ON nj.jobId = suj.jobId 
            WHERE nj.nodeId = :id 
            """)
    List<Integer> findNodeUserIds(Integer id);

    WorkflowNode findByParentIdAndDeleted(Integer parentId, int i);

    WorkflowNode findByParentIdAndDeletedAndIsCondition(Integer parentId, int i, int i1);
}
