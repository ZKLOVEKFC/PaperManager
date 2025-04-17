//用于接受全局异常，因为validation在校验正则表达式的时候如果接收异常，报错信息是    "error": "Internal Server Error",不知道哪里错了，
// 加入以后显示    "message": "register.password: 需要匹配正则表达式\"^\\S{5,16}$\""
package com.Kz.exception;

import com.Kz.pojo.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
        e.printStackTrace();
        return Result.error(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "操作失败");
    }
}
