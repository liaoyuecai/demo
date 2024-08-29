package com.demo.workflow.datasource.entity;

import com.demo.core.entity.TableBaseEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "workflow_node")
public class WorkflowNode {
    /**
     * id
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    private Integer workflowId;
    private Integer childWorkflowId;
    private Integer parentId;
    private Integer isCondition;
    private Integer isReturn;
    private Integer isUploadFile;
    private String nodeName;
    private Integer nodeType;
    private Integer deleted;

}