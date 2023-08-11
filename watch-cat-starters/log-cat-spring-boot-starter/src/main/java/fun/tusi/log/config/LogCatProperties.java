package fun.tusi.log.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * LogCat 配置
 * @author xy783
 */
@Data
@ConfigurationProperties(prefix = "watchcat.log")
public class LogCatProperties {

    private boolean enabled = false;

    private String ipKey = "";

}