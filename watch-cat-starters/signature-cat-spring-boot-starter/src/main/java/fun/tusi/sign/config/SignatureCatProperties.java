package fun.tusi.sign.config;

import cn.hutool.crypto.digest.HmacAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 接口签名验证参数配置
 * @author xy783
 */
@Data
@ConfigurationProperties(prefix = "watchcat.signature")
public class SignatureCatProperties {

    /**
     * 是否启用签名验证
     */
    private boolean enabled = true;

    private DigestSignProvider digest;

    @Data
    public static class DigestSignProvider {

        /**
         * 签名算法
         */
        private HmacAlgorithm algorithm = HmacAlgorithm.HmacSHA256;

        /**
         * 前后宽容时间
         */
        private Duration tolerant = Duration.ofSeconds(300);

        /**
         * 应用
         */
        private Map<String, String> apps = new HashMap<>();

    }

}