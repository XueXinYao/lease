package com.atguigu.lease.common.exception;

import com.atguigu.lease.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice    //使用Spring MVC提供的**全局异常处理**功能，可以将所有处理异常的逻辑集中起来，进而统一处理所有异常，使代码更容易维护
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handler(Exception e){
            e.printStackTrace();
            return Result.fail();
    }

}
