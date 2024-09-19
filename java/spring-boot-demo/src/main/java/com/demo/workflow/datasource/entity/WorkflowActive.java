package com.demo.workflow.datasource.entity;

import com.demo.core.entity.TableBaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "workflow_active")
public class WorkflowActive  {
    /**
     * id
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    private Integer nodeId;
    private String workflowName;
    //父流程ID  开启流程为子流程时生效
    private Integer parentWorkflowId;
    private Integer workflowId;
    private LocalDateTime createTime;
    private Integer createBy;
    private Integer updateBy;
    //1正常 0 结束
    private Integer status;
}