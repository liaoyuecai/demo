package com.demo.core.dto;


import com.demo.core.exception.ErrorCode;
import lombok.Data;

@Data
public class ApiHttpRequest<T> {

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


}
