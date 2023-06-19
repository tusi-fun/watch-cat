package com.cat.sign.service;

import java.io.Serializable;

/**
 * 签名异常
 * @author xy783
 */
public class SignCatException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -1660174411947378014L;

    public SignCatException(String message) {
        super(message);
    }
}