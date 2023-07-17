package fun.tusi.sensitive.config;

import fun.tusi.sensitive.aspect.SensitiveAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * watch-cat 配置
 * @author xy783
 */
@Configuration
@EnableConfigurationProperties({
        SensitiveCatProperties.class
})
public class SensitiveCatConfiguration {

    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "watchcat.sensitive", name = "enabled", havingValue = "true")
    @Bean
    public SensitiveAspect sensitiveAspect(){
        return new SensitiveAspect();
    }

}