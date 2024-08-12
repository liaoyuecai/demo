package com.demo.core.dto;


import com.demo.core.exception.ErrorCode;
import com.demo.core.utils.JsonUtils;
import lombok.Data;

import java.io.Serializable;

@Data
public class ApiHttpRequest<T> implements Serializable {

    private Long traceId;

    protected T data;


    public ApiHttpResponse success() {
        return new ApiHttpResponse(ErrorCode.SUCCESS);
    }

    public <K> ApiHttpResponse<K> success(K result) {
        return new ApiHttpResponse(result);
    }

    public ApiHttpResponse success(ErrorCode code) {
        return new ApiHttpResponse(code);
    }

    @Override
    public String toString() {
        return JsonUtils.toJsonStr(this);
    }
}
