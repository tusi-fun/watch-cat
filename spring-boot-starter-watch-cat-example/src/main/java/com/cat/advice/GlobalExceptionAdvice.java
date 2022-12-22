package com.cat.advice;

import com.cat.exception.BusinessException;
import com.cat.exception.EncryptErrEnum;
import com.cat.exception.LimitErrEnum;
import com.cat.exception.SysDefaultEnum;
import com.cat.result.Result;
import com.cat.watchcat.limit.service.LimitCatException;
import com.cat.watchcat.secret.service.SecretCatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 *
 * @author hudongshan
 * @version 20210608
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * watch-cat limit 流控异常
     */
    @ExceptionHandler(value = {LimitCatException.class})
    public Result exceptionHandler(LimitCatException e) {
        log.error("LimitCatException >> {}",e.getMessage());
        return Result.fail(LimitErrEnum.BUSINESS_LIMIT_CONTROL, e.getMessage());
    }

    /**
     * watch-cat encrypt 参数加解密异常
     */
    @ExceptionHandler(value = {SecretCatException.class})
    public Result exceptionHandler(SecretCatException e) {
        log.error("SecretCatException >> {}",e.getMessage());
        return Result.fail(EncryptErrEnum.ENCRYPT_CONTROL, e.getMessage());
    }

    /**
     * 参数验证异常 或 参数绑定异常
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result argumentExceptionHandler(Exception e) {
        log.error("MethodArgumentNotValidException or BindException >> {}",e.getMessage());
        BindingResult bindingResult = (e instanceof MethodArgumentNotValidException)?((MethodArgumentNotValidException)e).getBindingResult():((BindException)e).getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        Map<String,String> msgMap = new HashMap<>();
        for (int i = 0; i < fieldErrors.size(); i++) {

            FieldError fieldError = fieldErrors.get(i);

            // 获取参数名
            String fieldName = fieldError.getField();

            // 构建错误消息
            StringBuilder sb = new StringBuilder();

            // 验证同一参数是否存在其他错误消息，存在则直接拼接
            if(msgMap.containsKey(fieldName)) {
                sb.append(msgMap.get(fieldName)).append(" 且 ");
            } else {
                sb.append("参数 ").append(fieldName).append(" ");
            }

            sb.append(fieldError.getDefaultMessage());

            msgMap.put(fieldName,sb.toString());
        }

        return Result.fail(SysDefaultEnum.BAD_REQUEST,msgMap.values().stream().collect(Collectors.joining(",")));
    }

    /**
     * 参数类型异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = {TypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(TypeMismatchException e) {
        log.error("TypeMismatchException >> {}",e.getMessage());
        return Result.fail(SysDefaultEnum.BAD_REQUEST, "参数类型异常");
    }

    /**
     * 缺少请求参数异常（@PathVariable 和 @RequestParam 取值时）
     */
    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException >> {}",e.getMessage());
        return Result.fail(SysDefaultEnum.BAD_REQUEST, String.format("参数%s不能为空", e.getParameterName()));
    }

    /**
     * 缺少请求头参数异常（@RequestHeader 取值时）
     */
    @ExceptionHandler(value = {MissingRequestHeaderException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(MissingRequestHeaderException e) {
        log.error("MissingRequestHeaderException >> {}",e.getMessage());
        return Result.fail(SysDefaultEnum.BAD_REQUEST, String.format("请求头参数%s不能为空", e.getHeaderName()));
    }

    /**
     * 方法内参数不合法
     */
    @ExceptionHandler(value = {IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(IllegalArgumentException e) {
        log.error("IllegalArgumentException >> {}",e.getMessage());
        e.printStackTrace();
        return Result.fail(SysDefaultEnum.BAD_REQUEST,e.getMessage());
    }

    /**
     * 日期参数格式不正确
     */
    @ExceptionHandler(value = {DateTimeParseException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(DateTimeParseException e) {
        log.error("DateTimeParseException >> {}",e.getMessage());
        return Result.fail(SysDefaultEnum.BAD_REQUEST,"日期格式不正确");
    }

    /**
     * 请求方式不正确
     */
    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result exceptionHandler(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException >> {}",e.getMessage());
        return Result.fail(SysDefaultEnum.METHOD_NOT_ALLOWED);
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(value = {BusinessException.class})
    public Result exceptionHandler(BusinessException e) {
        log.error("BusinessException >> {}",e.getMessage());
        e.printStackTrace();
        return Result.fail(e.getBaseCode(),e.getMessage());
    }

    /**
     * 500 内部异常
     */
    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result exceptionHandler(Exception e) {
        log.error("Exception >> {}",e.getMessage());
        e.printStackTrace();
        return Result.fail(SysDefaultEnum.INTERNAL_SERVER_ERROR);
    }

//    /**
//     * 数据库数据未找到错误
//     */
//    @ExceptionHandler(value = {EmptyResultDataAccessException.class})
//    public Result exceptionHandler(EmptyResultDataAccessException e) {
//        log.error("EmptyResultDataAccessException >> {}",e.getMessage());
//        e.printStackTrace();
//        return Result.fail(BusinessCodeEnum.EMPTY_RESULT_DATA_ACCESS);
//    }
//
//    /**
//     * 数据违反数据库完整性约束
//     */
//    @ExceptionHandler(value = {DataIntegrityViolationException.class, JpaSystemException.class})
//    public Result sqlExceptionHandler(Exception e) {
//        log.error("DataIntegrityViolationException or JpaSystemException >> {}",e.getMessage());
//        e.printStackTrace();
//        return Result.fail(BusinessCodeEnum.DATA_INTEGRITY_VIOLATION,e.getMessage());
//    }


}