package com.demo.workflow.datasource.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "workflow_distribute")
public class WorkflowDistribute {
    /**
     * id
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    private Integer workflowId;
    private Integer userId;

    public WorkflowDistribute() {
    }

    public WorkflowDistribute(Integer workflowId, Integer userId) {
        this.workflowId = workflowId;
        this.userId = userId;
    }
}