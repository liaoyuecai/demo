package com.demo.workflow.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.workflow.datasource.dto.WorkflowHistoryDto;
import com.demo.workflow.datasource.entity.WorkflowActiveHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowActiveHistoryRepository extends CustomerBaseRepository<WorkflowActiveHistory> {


    WorkflowActiveHistory findByNodeIdAndStatus(Integer nodeId, int i);

    @Query("""
            select new WorkflowHistoryDto(ha.id,ha.nodeId,wn.nodeName,ha.activeInput,
            ha.activeFile,ha.activeStatus,ha.createTime,ha.createBy,u.realName) 
            from WorkflowActiveHistory ha 
            Join WorkflowNode wn ON wn.id = ha.nodeId
            Join SysUser u ON u.id = ha.createBy
            where wn.workflowId = :workflowId and ha.status = 1
            """)
    List<WorkflowHistoryDto> findDtoByWorkflowId(Integer workflowId);

    @Query("""
            select new WorkflowHistoryDto(ha.id,ha.nodeId,wn.nodeName,ha.activeInput,
            ha.activeFile,ha.activeStatus,ha.createTime,ha.createBy,u.realName) 
            from WorkflowActiveHistory ha 
            Join WorkflowNode wn ON wn.id = ha.nodeId
            Join SysUser u ON u.id = ha.createBy
            where wn.workflowId = :workflowId and ha.status = 0
            """)
    WorkflowHistoryDto findActiveDtoByWorkflowId(Integer workflowId);
}
