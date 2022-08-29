package com.jymj.mall.common.exception;

import com.jymj.mall.common.result.IResultCode;
import lombok.Getter;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-08
 */
@Getter
public class BusinessException extends RuntimeException  {

    public IResultCode resultCode;

    public BusinessException(IResultCode errorCode) {
        super(errorCode.getMsg());
        this.resultCode = errorCode;
    }

    public BusinessException(String message){
        super(message);
    }

    public BusinessException(String message, Throwable cause){
        super(message, cause);
    }

    public BusinessException(Throwable cause){
        super(cause);
    }


}
