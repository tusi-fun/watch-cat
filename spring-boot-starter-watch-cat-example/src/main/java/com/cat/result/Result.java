package com.cat.result;

import com.cat.exception.BaseCode;
import com.cat.exception.SysDefaultEnum;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 响应基类
 *
 * @author hudongshan
 * @version 20181107
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 498230420181686615L;

    protected static final BaseCode SUCCESS = SysDefaultEnum.SUCCESS;

    /**
     * 业务状态码（200为成功）
     * @mock 200
     */
    private Integer code;

    private String message;

    private T data;

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
    public Result data(T data) {
        this.setData(data);
        return this;
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