package com.example.myusercenterback.exception;

import com.example.myusercenterback.common.BaseResponse;
import com.example.myusercenterback.common.ErrorCode;
import com.example.myusercenterback.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author:xxxxx
 * @create: 2023-06-25 09:30
 * @Description: 全局异常处理器 防止直接throw异常 前台报错信息更友好 同时不暴漏后台代码结构 也方便同一处理异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessException(BusinessException exception) {
        log.error("businessException",exception);
        return ResultUtils.error(exception.getCode(),exception.getMessage(),exception.getDescription());    }

    //捕获系统内的500异常 集中处理
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeException(RuntimeException exception) {
        log.error("runtimeException",exception);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR);    }

}
