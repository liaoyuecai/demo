package com.demo.workflow.datasource.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class WorkflowRecordTypeDto {

    @Id
    private Integer id;
    private String workflowName;
    private String typeName;
    private Integer typeId;

    public WorkflowRecordTypeDto() {
    }

    public WorkflowRecordTypeDto(Integer id, String workflowName, String typeName, Integer typeId) {
        this.id = id;
        this.workflowName = workflowName;
        this.typeName = typeName;
        this.typeId = typeId;
    }
}