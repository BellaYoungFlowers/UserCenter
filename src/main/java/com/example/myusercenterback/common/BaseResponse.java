package com.example.myusercenterback.common;

import java.io.Serializable;

/**
 * @author:wtt
 * @create: 2023-06-21 15:49
 * @Description: 通用的返回类
 */
public class BaseResponse<T> implements Serializable {
    public String code;
    public T data;
    public String message;
    public String description;

    public BaseResponse(String code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }
    public BaseResponse(String code, T data, String message) {
       this(code, data,message,"");
    }
    public BaseResponse(String code, T data) {
        this(code, data,"","");
    }
    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null, errorCode.getMessage(), errorCode.getDescription());
    }
    private static final long serialVersionUID = 1905122041950251207L;
}
