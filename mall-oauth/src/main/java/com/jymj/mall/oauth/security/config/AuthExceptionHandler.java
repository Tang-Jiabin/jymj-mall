package com.jymj.mall.oauth.security.config;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 授权异常处理
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-16
 */
@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

    /**
     * 用户名和密码错误
     *
     */
    @ExceptionHandler(InvalidGrantException.class)
    public Result handleInvalidGrantException(InvalidGrantException e) {
        e.printStackTrace();
        return Result.failed(ResultCode.USERNAME_OR_PASSWORD_ERROR);
    }

    /**
     * 用户不存在
     */
    @ExceptionHandler({UsernameNotFoundException.class})
    public Result usernameNotFoundException(UsernameNotFoundException e) {
        e.printStackTrace();
        return Result.failed(e.getMessage());
    }

    /**
     * 用户被禁用
     */
    @ExceptionHandler({DisabledException.class})
    public Result disabledException(DisabledException e) {
        e.printStackTrace();
        return Result.failed(e.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public Result Exception(Exception e)  {
        e.printStackTrace();
        return Result.failed(e.getMessage());
    }

    @ExceptionHandler(NoSuchClientException.class)
    public Result noSuchClientException(NoSuchClientException e)  {
        e.printStackTrace();
        return Result.failed(e.getMessage());
    }
}
