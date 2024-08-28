package com.demo.workflow.datasource.entity;

import com.demo.core.entity.TableBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "workflow_record")
public class WorkflowRecord extends TableBaseEntity {

    private String workflowName;
    private Integer workflowStatus;
    private String workflowNodes;

}