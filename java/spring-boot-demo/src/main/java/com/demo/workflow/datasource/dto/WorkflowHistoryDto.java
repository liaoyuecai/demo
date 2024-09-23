package com.demo.workflow.datasource.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class WorkflowHistoryDto {
    @Id
    private Integer id;
    private Integer nodeId;
    private String nodeName;
    private String activeInput;
    private String activeFile;
    //流程状态：1正常流转 2 回退 3审批通过 4审批不通过
    private Integer activeStatus;
    private LocalDateTime createTime;
    private Integer createBy;
    private String createUser;

    public WorkflowHistoryDto() {
    }

    public WorkflowHistoryDto(Integer id, Integer nodeId, String nodeName, String activeInput, String activeFile, Integer activeStatus, LocalDateTime createTime, Integer createBy, String createUser) {
        this.id = id;
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.activeInput = activeInput;
        this.activeFile = activeFile;
        this.activeStatus = activeStatus;
        this.createTime = createTime;
        this.createBy = createBy;
        this.createUser = createUser;
    }
}