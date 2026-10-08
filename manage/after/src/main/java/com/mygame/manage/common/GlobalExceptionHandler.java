package com.mygame.manage.common;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ApiResponse<Void> handleApiException(ApiException exception) {
        return ApiResponse.fail(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException exception) {
        if (exception.getBindingResult().hasFieldErrors()) {
            return ApiResponse.fail(exception.getBindingResult().getFieldErrors().get(0).getDefaultMessage());
        }
        return ApiResponse.fail("请求参数校验失败");
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception exception) {
        return ApiResponse.fail(exception.getMessage() == null ? "服务器内部错误" : exception.getMessage());
    }
}
