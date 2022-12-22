package com.cat.exception;

import lombok.Getter;

/**
 * 参数加解密错误码和错误说明（非最终用户，仅供前后端开发使用）
 *
 * @author hudongshan
 * @version 20211122
 */
@Getter
public enum EncryptErrEnum implements BaseCode {

    // 业务调用频率异常
    ENCRYPT_CONTROL(5000, "%s");

    private final Integer code;
    private final String description;

    EncryptErrEnum(Integer code, String description) {
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