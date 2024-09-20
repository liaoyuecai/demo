package com.demo.workflow.datasource.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "workflow_node_cc_user")
public class WorkflowNodeCCUser {
    /**
     * id
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    private Integer nodeId;
    private Integer userId;

    public WorkflowNodeCCUser() {
    }

    public WorkflowNodeCCUser(Integer nodeId, Integer userId) {
        this.nodeId = nodeId;
        this.userId = userId;
    }
}