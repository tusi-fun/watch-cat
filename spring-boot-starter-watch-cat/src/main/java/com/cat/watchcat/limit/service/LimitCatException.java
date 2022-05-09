package com.cat.watchcat.limit.service;

import java.io.Serializable;

/**
 * 流控异常
 * @author hudongshan
 */
public class LimitCatException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -1660174411947378014L;

    public LimitCatException(String message) {
        super(message);
    }
}