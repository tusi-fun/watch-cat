package com.cat.common;

import com.cat.util.StringUtils;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 请求日志基类
 * @author hudongshan
 */
@Data
@SuperBuilder
public class RequestInfo implements Serializable {

    private static final long serialVersionUID = -5874472232861587583L;

    private String bid;
    private String httpMethod;
    private String url;
    private Map<String, Object> requestParams;
    private Map<String, Object> requestHeaders;
    private String classMethod;
    private String ip;
    private Long timeCost;
    private String actionGroup;
    private String action;
    private Object result;
    private String exception;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Boolean itsOk() {
        return StringUtils.hasText(exception);
    }
}
