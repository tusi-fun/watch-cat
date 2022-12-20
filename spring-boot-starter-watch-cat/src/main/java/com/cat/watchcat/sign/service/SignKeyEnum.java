package com.cat.watchcat.sign.service;

/**
 * 签名参数枚举
 * @author hudongshan
 */

public enum SignKeyEnum {
    APPID_KEY("appid"),
    SECRET_KEY("secret"),
    PATH_KEY("path"),
    CONTENT_MD5_KEY("content-md5"),
    METHOD_KEY("method"),
    TIMESTAMP_KEY("timestamp"),
    NONCE_KEY("nonce"),
    SIGN_KEY("sign");

    public String value;

    SignKeyEnum(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
