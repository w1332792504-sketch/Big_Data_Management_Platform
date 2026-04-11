package com.datamanager.exception;

/**
 * 资源未找到异常
 */
public class NotFoundException extends GlobalException {

    public NotFoundException(String message) {
        super(404, message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(404, message, cause);
    }
}
