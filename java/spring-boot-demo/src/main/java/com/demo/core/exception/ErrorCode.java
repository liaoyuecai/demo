package com.demo.core.exception;

public enum ErrorCode {

    SUCCESS(0, "请求成功"),

    SYS_UNKNOWN_ERROR(1002, "系统异常"),
    SYS_SERVICE_HYSTRIX(1003, "系统熔断"),

    SYS_TIME_OUT(1201, "系统超时"),

    HTTP_REQUEST_FAILED(2001, "http请求失败"),
    HTTP_URL_NOT_FOUND(2002, "http请求路由异常"),

    ACCESS_PASSWORD_ERROR(3001, "用户名或密码错误"),
    ACCESS_TOKEN_ERROR(3002, "token异常"),
    ACCESS_AUTHORITY_ERROR(3003, "无访问权限"),
    ACCESS_CHECK_CODE_ERROR(3004, "校验码异常"),
    ACCESS_ACCOUNT_LOCK_ERROR(3005, "账号锁定"),

    DATASOURCE_CONNECT_ERROR(-1000, "数据库连接异常"),
    PARAMS_ERROR(4001, "参数异常"),
    PARAMS_ERROR_DATA_NOT_FOUND(400101, "当前参数查询不到数据"),
    PARAMS_ERROR_OLD_PWD(400102, "旧密码错误"),
    PARAMS_ERROR_REQUEST_DATA_NOT_FOUND(400103, "请求参数为空"),
    PARAMS_ERROR_REQUEST_PARAMS_LESS(400104, "缺失请求参数"),
    CODE_ERROR(4002, "代码异常"),
    CODE_ERROR_PARAMS_NOT_FOUND(400201, "找不到对应的参数"),
    CODE_ERROR_PARAMS_TYPE_ERROR(400202, "参数类型异常"),
    CODE_ERROR_CLASS_ENTITY_ERROR(400203, "class 转换实体异常"),
    DATA_ERROR(4003, "数据异常"),

    OPERATE_OUT_RANGE_ERROR(5001, "操作越权"),
    ;

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public boolean success() {
        switch (this) {
            case SUCCESS:
                return true;
            default:
                return false;
        }
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
