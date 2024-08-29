package com.demo.workflow.datasource.dto;

import com.demo.workflow.datasource.entity.WorkflowNodeInput;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkflowSaveNode {
    private Integer key;
    private Integer type;
    private Integer startNode;
    private Integer endNode;
    private Integer childWorkflowId;
    private String name;
    private List<Integer> userIds;
    private List<Integer> ccUserIds;
    private List<Integer> jobIds;
    private List<WorkflowNodeInput> inputs;
    private boolean isReturn;
    private boolean isCondition;
    private boolean isUploadFile;
}
