package com.demo.workflow.datasource.dto;

import com.demo.workflow.datasource.entity.WorkflowNode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkflowInputAndData {
    private WorkflowNode node;
    private List<NodeInputData> inputs;
    private String filePath;
}
