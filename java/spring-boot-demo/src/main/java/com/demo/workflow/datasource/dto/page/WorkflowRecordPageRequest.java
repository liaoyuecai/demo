package com.demo.workflow.datasource.dto.page;

import com.demo.core.dto.PageListRequest;
import com.demo.core.utils.JpaQueryHelper;
import com.demo.workflow.datasource.entity.WorkflowRecord;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.util.Map;

public class WorkflowRecordPageRequest extends PageListRequest<WorkflowRecord> {


    @Override
    public Example toExample() {
        return JpaQueryHelper.initExample(this.data, Map.of(
                "workflowName", ExampleMatcher.GenericPropertyMatchers.contains()
                ));
    }
}
