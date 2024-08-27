package com.demo.workflow.datasource.dao;

import com.demo.core.config.jpa.CustomerBaseRepository;
import com.demo.workflow.datasource.entity.WorkflowRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRecordRepository extends CustomerBaseRepository<WorkflowRecord> {


}
