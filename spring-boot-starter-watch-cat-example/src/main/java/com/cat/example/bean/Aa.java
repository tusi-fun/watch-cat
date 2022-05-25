package com.cat.example.bean;

import cn.hutool.core.util.DesensitizedUtil.DesensitizedType;
import com.cat.watchcat.sensitive.annotation.SensitiveField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;


/**
 * @author hudongshan
 * @version 2021/11/22
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Aa implements Serializable {

    private static final long serialVersionUID = 659863927348642929L;

    String securityCode;

    //	@NotBlank
    @SensitiveField(type = DesensitizedType.MOBILE_PHONE)
    String phone;

    @SensitiveField(type = DesensitizedType.ADDRESS)
    String address;

    @SensitiveField(type = DesensitizedType.ID_CARD)
    String idCard;

    @SensitiveField(type = DesensitizedType.EMAIL)
    String email;

    @SensitiveField(type = DesensitizedType.PASSWORD)
    String password;

    @SensitiveField(type = DesensitizedType.CHINESE_NAME)
    String name;

}