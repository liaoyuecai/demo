package com.demo.core.authentication;

import lombok.Getter;

/**
 * 验证返回格式实体
 * @param <T>
 */
@Getter
public class AuthenticationResponse<T> {
    private final static int LOGIN_ERROR = 3001;
    private final static int TOKEN_ERROR = 3002;

    private final int code;
    private String errorMsg;
    private T data;

    public AuthenticationResponse(int code) {
        this.code = code;
    }

    public AuthenticationResponse(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public AuthenticationResponse(int code, String message) {
        this.code = code;
        this.errorMsg = message;
    }

    public AuthenticationResponse(int code, String message, T data) {
        this.code = code;
        this.errorMsg = message;
        this.data = data;
    }

    public static AuthenticationResponse success() {
        return new AuthenticationResponse(0);
    }


    public static AuthenticationResponse success(String message) {
        return new AuthenticationResponse(0, message);
    }

    public static <T> AuthenticationResponse<T> success(String message, T data) {
        return new AuthenticationResponse(0, message, data);
    }

    public static <T> AuthenticationResponse<T> success(T data) {
        return new AuthenticationResponse(0, data);
    }

    public static AuthenticationResponse<AuthenticationUser> loginSuccess(AuthenticationUser user) {
        return new AuthenticationResponse(0, user);
    }

    public static AuthenticationResponse loginError(String message) {
        return new AuthenticationResponse(LOGIN_ERROR, message);
    }

    public static AuthenticationResponse tokenError(String message) {
        return new AuthenticationResponse(TOKEN_ERROR, message);
    }
}
