package com.demo.core.dto;


import com.demo.core.exception.ErrorCode;
import com.demo.core.exception.GlobalException;
import com.demo.core.utils.JsonUtils;

import java.io.Serializable;

public class ApiHttpResponse<T> implements Serializable {
    private Long traceId;
    private final int code;
    private final ErrorCode errorCode;
    private String errorMsg;
    private T data;

    public int getCode() {
        return code;
    }

    public Long getTraceId() {
        return traceId;
    }

    public void setTraceId(Long traceId) {
        this.traceId = traceId;
    }


    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ApiHttpResponse() {
        this.errorCode = ErrorCode.SUCCESS;
        this.code = ErrorCode.SUCCESS.getCode();
        this.errorMsg = ErrorCode.SUCCESS.getMessage();
    }


    public ApiHttpResponse(T data) {
        this.errorCode = ErrorCode.SUCCESS;
        this.code = ErrorCode.SUCCESS.getCode();
        this.errorMsg = ErrorCode.SUCCESS.getMessage();
        this.data = data;
    }


    public ApiHttpResponse(ErrorCode code) {
        this.errorCode = code;
        this.code = code.getCode();
        this.errorMsg = code.getMessage();
    }

    public ApiHttpResponse(ErrorCode code, Throwable e) {
        this.errorCode = code;
        this.code = code.getCode();
        this.errorMsg = e.getMessage();
    }

    private ApiHttpResponse(ErrorCode code, String message) {
        this.errorCode = code;
        this.code = code.getCode();
        this.errorMsg = message;
    }

    private ApiHttpResponse(ErrorCode code, String message, T data) {
        this.errorCode = code;
        this.code = code.getCode();
        this.errorMsg = message;
        this.data = data;
    }

    public final static ApiHttpResponse success() {
        return new ApiHttpResponse();
    }

    public final static ApiHttpResponse error(ErrorCode code) {
        return new ApiHttpResponse(code, code.getMessage());
    }

    public final static ApiHttpResponse error(GlobalException e) {
        return new ApiHttpResponse(e.getCode(), e.getMessage());
    }

    public final static ApiHttpResponse error(ErrorCode code, String message) {
        return new ApiHttpResponse(code, message);
    }

    public final static <T> ApiHttpResponse<T> error(ErrorCode code, String message, T data) {
        return new ApiHttpResponse<T>(code, message, data);
    }

    public final static ApiHttpResponse success(Object result) {
        return new ApiHttpResponse(result);
    }
    @Override
    public String toString() {
        return JsonUtils.toJsonStr(this);
    }
}
