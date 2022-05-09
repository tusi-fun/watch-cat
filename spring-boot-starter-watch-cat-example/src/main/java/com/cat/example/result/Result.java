package com.cat.example.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result implements Serializable {

    private static final long serialVersionUID = 498230420181686615L;

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("message")
    private String message;

    public Result() {
        this(200,"success");
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Result ok() {
        return new Result();
    }


}