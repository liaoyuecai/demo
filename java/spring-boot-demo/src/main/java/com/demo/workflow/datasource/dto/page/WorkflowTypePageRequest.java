package com.demo.workflow.datasource.dto.page;

import com.demo.core.dto.PageListRequest;
import com.demo.core.utils.JpaQueryHelper;
import com.demo.workflow.datasource.entity.WorkflowType;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.util.Map;

public class WorkflowTypePageRequest extends PageListRequest<WorkflowType> {


    @Override
    public Example toExample() {
        return JpaQueryHelper.initExample(this.data, Map.of(
                "typeName", ExampleMatcher.GenericPropertyMatchers.contains()
        ));
    }
}
