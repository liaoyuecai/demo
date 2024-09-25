package com.demo.workflow.datasource.dto;

import com.demo.workflow.datasource.entity.WorkflowNode;
import com.demo.workflow.datasource.entity.WorkflowNodeInput;
import lombok.Data;

import java.util.List;


@Data
public class WorkflowEdit {
    private List<WorkflowHistoryDto> history;
    private WorkflowHistoryDto active;
    private WorkflowNode node;
    private List<WorkflowNodeInput> inputs;
}