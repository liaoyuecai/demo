package com.demo.workflow.datasource.dto;

import com.demo.workflow.datasource.entity.WorkflowNodeInput;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NodeInputData {
    private String inputTitle;
    private Integer inputNecessary;
    //类型 1日期时间 2 输入框 3长文本
    private Integer inputType;
    private String inputValue;

    public NodeInputData() {
    }

    public NodeInputData(WorkflowNodeInput input, String inputValue) {
        this.inputType = input.getInputType();
        this.inputTitle = input.getInputTitle();
        this.inputNecessary = input.getInputNecessary();
        this.inputValue = inputValue;
    }
}
