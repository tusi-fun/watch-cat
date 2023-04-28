package com.cat.secret.service;

import java.io.Serializable;

/**
 * 参数加解密异常
 * @author hudongshan
 */
public class SecretCatException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 3750361374511442674L;

    public SecretCatException(String message) {
        super(message);
    }
}