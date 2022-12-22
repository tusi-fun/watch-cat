package com.cat.exception;

import lombok.Getter;

/**
 * 错误码和错误说明（非最终用户，仅供前后端开发使用）
 *
 * @author hudongshan
 * @version 20210821
 */
@Getter
public enum SignErrEnum implements BaseCode {

    // watch-cat sign 异常
    SIGN_ERR(5000, "%s");

    private final Integer code;
    private final String description;

    SignErrEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public Integer code() {
        return code;
    }

    @Override
    public String desc() {
        return description;
    }

}