package com.demo.workflow.datasource.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "workflow_node_job")
public class WorkflowNodeJob {
    /**
     * id
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    private Integer nodeId;
    private Integer jobId;

    public WorkflowNodeJob(Integer nodeId, Integer jobId) {
        this.nodeId = nodeId;
        this.jobId = jobId;
    }
}