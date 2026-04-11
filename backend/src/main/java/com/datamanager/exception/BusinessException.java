package com.datamanager.exception;

/**
 * 业务异常类
 */
public class BusinessException extends GlobalException {

    public BusinessException(String message) {
        super(400, message);
    }

    public BusinessException(int code, String message) {
        super(code, message);
    }

    public BusinessException(String message, Throwable cause) {
        super(400, message, cause);
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
