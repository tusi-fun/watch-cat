package com.cat.result;

import com.cat.exception.BaseCode;
import lombok.Getter;

/**
 * 基础响应码（非最终用户，仅供前后端开发使用）
 *
 * @author hudongshan
 * @version 20211202
 */
@Getter
public enum SysDefaultEnum implements BaseCode {

    // basic
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "%s"),
    UNAUTHORIZED(401, "操作未授权（%s）"),
    METHOD_NOT_ALLOWED(405, "请求方法不正确"),
    INTERNAL_SERVER_ERROR(500, "服务内部异常");

    private final Integer code;
    private final String description;

    SysDefaultEnum(Integer code, String description) {
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