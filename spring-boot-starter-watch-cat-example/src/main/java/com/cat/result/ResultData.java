package com.cat.result;

import com.cat.exception.BaseCode;
import lombok.Getter;
import lombok.Setter;

/**
 * api 响应封装
 *
 * @author hudongshan
 * @version 20181107
 */
@Getter
@Setter
public class ResultData<T> extends Result {

    private T data;

    public ResultData() {
        super();
    }

    public ResultData(String message) {
        super(message);
    }

    public ResultData(int code, String message) {
        super(code, message);
    }

    public ResultData(BaseCode baseCode) {
        super(baseCode.code(), baseCode.desc());
    }

    public static ResultData ok() {
        return new ResultData();
    }

    public static ResultData ok(String message) {
        return new ResultData(message);
    }

    public ResultData data(T data) {
        this.setData(data);
        return this;
    }

}