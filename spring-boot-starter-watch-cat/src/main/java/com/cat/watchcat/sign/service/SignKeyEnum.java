package com.cat.watchcat.sign.service;

/**
 * 签名参数枚举
 * @author hudongshan
 */

public enum SignKeyEnum {
    APPID_KEY("xx-appid"),
    SECRET_KEY("token"),
    PATH_KEY("xx-path"),
    CONTENT_MD5_KEY("xx-content-md5"),
    METHOD_KEY("xx-method"),
    TIMESTAMP_KEY("xx-timestamp"),
    NONCE_KEY("xx-nonce"),
    SIGN_KEY("xx-sign");

    public String value;

    SignKeyEnum(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
