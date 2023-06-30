package com.example.myusercenterback.common;

/**
 * @author:xxxxx
 * @create: 2023-06-21 15:52
 * @Description: 返回结果工具类
 */
public class ResultUtils {
    public static<T> BaseResponse<T> success(T data){
        return new BaseResponse<T>("200",data,"ok");
    }
    public static BaseResponse error(ErrorCode errorCode){
        return new BaseResponse(errorCode);
    }
    public static BaseResponse error(String code,String message,String description){
        return new BaseResponse(code,message,description);
    }
}
