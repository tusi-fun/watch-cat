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

    private Map<String,Map<Duration,Long>> scenes;

}