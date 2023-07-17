package fun.tusi.sensitive.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 参数加解密配置
 * @author xy783
 */
@Data
@ConfigurationProperties(prefix = "watchcat.sensitive")
public class SensitiveCatProperties {

    private boolean enabled = false;

}