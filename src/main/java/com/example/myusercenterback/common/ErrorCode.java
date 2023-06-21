package com.example.myusercenterback.common;

/**
 * 错误码的枚举
 */
public enum ErrorCode {

    PARAMS_ERROR("40000","请求参数错误",""),
    NULL_ERROR("40001","请求参数为空",""),
    NOT_LOGIN("40100","未登录",""),
    NOT_AUTH("40101","无权限","");

    private final String code;
    private final String message;
    private final String description;

    ErrorCode(String code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
