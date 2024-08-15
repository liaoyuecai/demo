package com.demo.core.dto;


import com.demo.core.aop.RequestBaseEntitySet;
import com.demo.core.authentication.AuthenticationUser;
import com.demo.core.exception.ErrorCode;
import com.demo.core.utils.JsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ApiHttpRequest<T> implements Serializable {

    private Long traceId;

    protected T data;

    protected AuthenticationUser user;
    @JsonIgnore
    protected RequestBaseEntitySet entitySet;

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
