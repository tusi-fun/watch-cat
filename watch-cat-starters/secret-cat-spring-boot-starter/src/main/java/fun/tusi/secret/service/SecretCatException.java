package fun.tusi.secret.service;

import java.io.Serializable;

/**
 * 参数加解密异常
 * @author xy783
 */
public class SecretCatException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 3750361374511442674L;

    public SecretCatException(String message) {
        super(message);
    }
}