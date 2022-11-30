package com.cat.example.ping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


/**
 * 响应对象 ResultPing
 * @author hudongshan
 * @version 2021/11/22
 */
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultPing implements Serializable {

    private static final long serialVersionUID = -704408850324979989L;

    /**
     * 姓名
     */
    String name;

    /**
     * 手机号
     */
    String phone;
}