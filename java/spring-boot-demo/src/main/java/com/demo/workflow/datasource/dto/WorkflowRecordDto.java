package com.demo.workflow.datasource.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class WorkflowRecordDto {

    @Id
    private Integer id;
    private String workflowName;

    public WorkflowRecordDto(Integer id, String workflowName) {
        this.id = id;
        this.workflowName = workflowName;
    }
}