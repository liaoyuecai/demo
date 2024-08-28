package com.demo.workflow.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.workflow.datasource.dto.WorkflowRecordDto;
import com.demo.workflow.datasource.entity.WorkflowRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowRecordRepository extends CustomerBaseRepository<WorkflowRecord> {


    @Query("""
            select new WorkflowRecordDto(t.id,t.workflowName) from WorkflowRecord t 
            where t.deleted = 0 and t.status = 1 and t.workflowStatus = 1
            """)
    List<WorkflowRecordDto> findRecordsDto();
}
