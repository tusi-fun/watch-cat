package com.cat.example.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultData<T> extends Result {

    @JsonProperty("data")
    private T data;

    public ResultData(int code, String message) {
        super(code, message);
    }
    public ResultData data(T data) {
        this.setData(data);
        return this;
    }

}