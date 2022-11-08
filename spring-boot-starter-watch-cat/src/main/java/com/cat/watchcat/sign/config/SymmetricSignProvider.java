package com.cat.watchcat.sign.config;

import lombok.Data;

/**
 * 接口签名验证参数配置
 * @author hudongshan
 */
@Data
public class SymmetricSignProvider {

    /**
     * 签名算法
     */
    private String algorithm;

    /**
     * 前后宽容时间(s)
     */
    private Long tolerant;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 私钥
     */
    private String privateKey;

}