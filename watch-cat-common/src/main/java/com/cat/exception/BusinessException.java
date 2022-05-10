package com.cat.exception;

import java.io.Serializable;

/**
 * 业务异常
 * @author hudongshan
 */
public class BusinessException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -1041148006846583767L;

    private final BaseCode baseCode;

    public BusinessException(BaseCode baseCode, String... args) {
        super(String.format(baseCode.desc(), args));
        this.baseCode = baseCode;
    }

    public BaseCode getBaseCode() {
        return baseCode;
    }

    public int getCode() {
        return baseCode.code();
    }
}