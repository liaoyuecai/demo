package com.demo.workflow.datasource.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "workflow_active_history")
public class WorkflowActiveHistory {
    /**
     * id
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    private Integer nodeId;
    private Integer parentId;
    private String activeInput;
    private String activeFile;
    //状态 0 正在编辑 1 已经完成
    private Integer status;
    //流程状态：1正常流转 2 回退 3审批通过 4审批不通过
    private Integer activeStatus;
    private LocalDateTime createTime;
    private Integer createBy;

}