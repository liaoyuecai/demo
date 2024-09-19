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
    //节点类型 1开始，2结束，3任务节点，4子流程节点，5决策节点
    private Integer nodeType;
    private Integer deleted;

}