package com.example.javaee_ecomorder.common.exception;

import com.example.javaee_ecomorder.common.utils.Result;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public Result<Void> handleUnauthorizedException(UnauthorizedException e) {
        return Result.unauthorized(e.getMessage());
    }

    @ExceptionHandler(PermissionDeniedException.class)
    public Result<Void> handlePermissionDeniedException(PermissionDeniedException e) {
        return Result.forbidden(e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        if (e instanceof LockedException) {
            return Result.error("账户已锁�?);
        }
        if (e instanceof BadCredentialsException) {
            return Result.error("用户名或密码错误");
        }
        return Result.unauthorized("未登录或Token过期");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        return Result.forbidden("无权�?);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        e.printStackTrace();
        return Result.error("系统异常�? + e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("�?));
        return Result.error(errorMsg);
    }
}