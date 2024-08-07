package com.demo.core.utils;

import com.demo.core.exception.ErrorCode;
import com.demo.core.exception.GlobalException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.util.Map;

public final class JpaQueryHelper {

    public static <T> Example initExample(T queryEntity, Map<String, ExampleMatcher.GenericPropertyMatcher> matcherMap) {
        if (queryEntity == null)
            throw new GlobalException(ErrorCode.PARAMS_ERROR_REQUEST_DATA_NOT_FOUND);
        ExampleMatcher matching = ExampleMatcher.matching();
        for (String key : matcherMap.keySet())
            matching = matching.withMatcher(key, matcherMap.get(key));
        return Example.of(queryEntity, matching);
    }

}
