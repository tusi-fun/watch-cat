package fun.tusi.sign.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Map;

/**
 * 接口签名验证参数配置（适用于非对称算法rsa、sm2 和 hash）
 * @author xy783
 */
@Data
@ConfigurationProperties(prefix = "watchcat.signature")
public class SignatureCatProperties {

    private boolean enabled = false;

    private Map<String, Map<Duration, SignatureCatRule>> scenes;

    @Data
    public static class SignatureCatRule {

        /**
         * 签名算法
         */
        private String algorithm;

        /**
         * 前后宽容时间
         */
        private Duration tolerant;
    }








    private Map<String,SymmetricSignProvider> symmetric;

    private ShaSignProvider sha;

    @Data
    public static class SymmetricSignProvider {
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

    @Data
    public static class ShaSignProvider {

        private boolean enabled = false;

        /**
         * 签名算法
         */
        private String algorithm;

        /**
         * 前后宽容时间
         */
        private Duration tolerant;

    }

}