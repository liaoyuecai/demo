package com.demo.core.exception;


/**
 * 业务上可以识别的异常
 */
public class GlobalException extends RuntimeException {
    ErrorCode code;

    public GlobalException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }


    public GlobalException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }

    public int getCodeValue() {
        return code.getCode();
    }
}
