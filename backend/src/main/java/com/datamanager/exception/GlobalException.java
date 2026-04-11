package com.datamanager.exception;

import lombok.Getter;

/**
 * 全局自定义异常基类
 */
@Getter
public class GlobalException extends RuntimeException {

    private final int code;

    public GlobalException(String message) {
        super(message);
        this.code = 500;
    }

    public GlobalException(int code, String message) {
        super(message);
        this.code = code;
    }

    public GlobalException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    public GlobalException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
