package com.cat.enumerate;

/**
 * 签名参数枚举
 * @author hudongshan
 */

public enum ApiSignKeyEnum {
    APPID_KEY("appid"),
    SECRET_KEY("secret"),
    CONTENT_MD5_KEY("content-md5"),
    TIMESTAMP_KEY("timestamp"),
    NONCE_KEY("nonce"),
    SIGN_KEY("sign"),
    METHOD_KEY("method"),
    PATH_KEY("path");

    public String value;

    ApiSignKeyEnum(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
