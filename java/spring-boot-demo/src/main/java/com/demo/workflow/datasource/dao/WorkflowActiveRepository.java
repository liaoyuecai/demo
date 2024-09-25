package com.demo.workflow.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.core.dto.PageList;
import com.demo.workflow.datasource.dto.WorkflowActiveDto;
import com.demo.workflow.datasource.entity.WorkflowActive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowActiveRepository extends CustomerBaseRepository<WorkflowActive> {


    WorkflowActive findByWorkflowIdAndNodeId(Integer workflowId, Integer nodeId1);
    @Modifying
    @Query("""
            update WorkflowActive wa set wa.updateBy = :userId 
            where wa.id = :id and wa.updateBy is null
            """)
    int updateUpdateBy(Integer userId, Integer id);
    @Query("""
            select new WorkflowActiveDto(wa.id,wa.workflowId,wa.nodeId,wa.workflowName,wn.nodeName,wa.createTime,wa.status) from WorkflowActive wa 
            Join WorkflowNode wn ON wa.nodeId = wn.id
            where wa.createBy = :userId and (:workflowName is null or wa.workflowName like CONCAT('%', :workflowName, '%'))
            """)
    Page<WorkflowActiveDto> findDtoPage(String workflowName,Integer userId, Pageable pageable);

    @Query("""
            select new WorkflowActiveDto(wa.id,wa.workflowId,wa.nodeId,wa.workflowName) from WorkflowActive wa 
            Join WorkflowDistribute wd ON wa.workflowActiveId = wd.id
            where wd.userId = :userId and wa.status = 1
            """)
    Page<WorkflowActiveDto> findWorkDtoPage(Integer userId, Pageable pageable);
}
