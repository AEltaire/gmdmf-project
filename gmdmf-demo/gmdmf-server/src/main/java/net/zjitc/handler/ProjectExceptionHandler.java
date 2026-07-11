package net.zjitc.handler;

import io.swagger.v3.oas.annotations.Hidden;
import net.zjitc.constant.Code;
import net.zjitc.exception.BusinessException;
import net.zjitc.exception.SystemException;
import net.zjitc.result.Result;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ProjectExceptionHandler {

    // 拦截系统异常
    @ExceptionHandler(SystemException.class)
    public Result<Object> handSystemException(Exception e) {
        return Result.error(Code.SYSTEM_ERR, "系统繁忙，请稍后再试");
    }

    // 拦截业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<Object> handBusinessException(Exception e) {
        return Result.error(Code.BUSINESS_ERR, e.getMessage());
    }

    // 拦截参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> validationBodyException(MethodArgumentNotValidException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        return Result.error(Code.BUSINESS_ERR, objectError.getDefaultMessage());
    }

    // 拦截所有异常
    @ExceptionHandler(Exception.class)
    public Result<Object> handException(Exception e) {
        return Result.error(Code.OTHER_ERR, "系统繁忙，请稍后再试");
    }
}
