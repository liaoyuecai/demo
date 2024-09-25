package com.demo.workflow.datasource.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "workflow_distribute_cc")
public class WorkflowDistributeCC {
    /**
     * id
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    private Integer workflowActiveId;
    private Integer nodeHistoryId;
    private Integer userId;

    public WorkflowDistributeCC() {
    }

    public WorkflowDistributeCC(Integer workflowActiveId, Integer nodeHistoryId, Integer userId) {
        this.workflowActiveId = workflowActiveId;
        this.nodeHistoryId = nodeHistoryId;
        this.userId = userId;
    }
}