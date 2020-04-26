package com.atguigu.gmall.common.exception.handler;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.exception.GmallException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GmallException.class)
    public ResponseVo<Object> gmallException(Exception e){
        return ResponseVo.fail(500,e.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseVo<Object> exception(Exception e){
        return ResponseVo.fail(500,e.getMessage());
    }
}
