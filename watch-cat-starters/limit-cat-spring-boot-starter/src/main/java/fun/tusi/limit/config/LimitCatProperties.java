package fun.tusi.limit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Map;

/**
 * 业务调用流控配置
 * @author xy783
 */
@Data
@ConfigurationProperties(prefix = "watchcat.limit")
public class LimitCatProperties {

    private boolean enabled = false;
    private String defaultMessage = "操作太频繁，请稍后再试。（场景%s限制%s执行%s次）";

    private Map<String, Map<Duration, LimitRule>> scenes;

    @Data
    public static class LimitRule {

        private Long frequency;

        private String message;
    }

}