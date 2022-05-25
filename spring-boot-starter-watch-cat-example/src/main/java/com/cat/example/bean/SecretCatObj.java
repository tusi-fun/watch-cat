package com.cat.example.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;


/**
 * @author hudongshan
 * @version 2021/11/22
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecretCatObj implements Serializable {

    private static final long serialVersionUID = 7780554602940979789L;

    String securityCode;
    String phone;
    String address;
    String idCard;
    String email;
    String password;
    String name;

}