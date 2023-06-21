package com.example.myusercenterback.exception;

import com.example.myusercenterback.common.ErrorCode;

/**
 * @author:xxxxx
 * @create: 2023-06-21 17:04
 * @Description: 业务上的报错异常
 */
public class BusinessException extends RuntimeException {
    private final String code;
    private final String description;

    public BusinessException(String message, String code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

}
