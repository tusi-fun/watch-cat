package fun.tusi.limit.service;

import java.io.Serializable;

/**
 * 流控异常
 * @author xy783
 */
public class LimitCatException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -1660174411947378014L;

    public LimitCatException(String message) {
        super(message);
    }
}