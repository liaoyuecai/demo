package com.demo.workflow.datasource.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class WorkflowActiveDto {
    @Id
    private Integer id;
    private Integer workflowId;
    private Integer nodeId;
    private String workflowName;
    private String nodeName;
    private LocalDateTime createTime;
    private Integer status;

    public WorkflowActiveDto() {
    }

    public WorkflowActiveDto(Integer id, Integer workflowId, Integer nodeId, String workflowName) {
        this.id = id;
        this.workflowId = workflowId;
        this.nodeId = nodeId;
        this.workflowName = workflowName;
    }

    public WorkflowActiveDto(Integer id, Integer workflowId, Integer nodeId, String workflowName, String nodeName, LocalDateTime createTime, Integer status) {
        this.id = id;
        this.workflowId = workflowId;
        this.nodeId = nodeId;
        this.workflowName = workflowName;
        this.nodeName = nodeName;
        this.createTime = createTime;
        this.status = status;
    }

}