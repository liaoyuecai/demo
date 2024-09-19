package com.demo.workflow.datasource.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SaveHistory {
    private Integer workflowId;
    private Integer nodeId;
    private List<String> inputs;
    //1 正常流转 2 回退 3审核通过 4审核不通过
    private Integer activeStatus;
}
