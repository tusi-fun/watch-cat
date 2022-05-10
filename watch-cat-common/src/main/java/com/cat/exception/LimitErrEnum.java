package com.cat.exception;

import lombok.Getter;

/**
 * 错误码和错误说明（非最终用户，仅供前后端开发使用）
 *
 * @author hudongshan
 * @version 20210821
 */
@Getter
public enum LimitErrEnum implements BaseCode {

    // watch-cat encrypt 参数加解密异常
    BUSINESS_LIMIT_CONTROL(5000, "%s"),
    BUSINESS_LIMIT_CONTROL_UNDEFINED(5001, "频率限制场景%s不存在或未配置");

    private final Integer code;
    private final String description;

    LimitErrEnum(Integer code, String description) {
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