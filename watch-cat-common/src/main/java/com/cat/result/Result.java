package com.cat.result;

import com.cat.exception.BaseCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 响应基类
 *
 * @author hudongshan
 * @version 20181107
 */
@Getter
@Setter
public class Result implements Serializable {

    private static final long serialVersionUID = 498230420181686615L;

    protected static final BaseCode SUCCESS = SysDefaultEnum.SUCCESS;

    private Integer code;

    private String message;

    public Result() {
        this(SUCCESS.code(), SUCCESS.desc());
    }

    public Result(String message) {
        this(SUCCESS.code(), message);
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Result ok() {
        return new Result();
    }

    public static Result ok(String message) {
        return new Result(message);
    }

    public static Result fail(BaseCode baseCode, String note) {
        return new Result(baseCode.code(), note);
    }

    public static Result fail(BaseCode baseCode) {
        return new Result(baseCode.code(), baseCode.desc());
    }

}