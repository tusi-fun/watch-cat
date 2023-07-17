package fun.tusi.log.config;

import fun.tusi.log.aspect.LogCatAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * watch-cat 配置
 * @author xy783
 *
 * - @ConditionalOnMissingBean
 * 修饰bean的注解，主要实现当bean被注册之后，再注册相同类型的bean，就不会成功，
 * 它会保证你的bean只有一个，即你的实例只有一个，当你注册多个相同的bean时，会出现异常
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({
        LogCatProperties.class,
})
public class LogCatConfiguration {

    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "watchcat.log", name = "enabled", havingValue = "true")
    @Bean
    public LogCatAspect logCatAspect() {

        log.info("Initializing {} > {}", this.getClass().getSimpleName(), "logCatAspect");

        return new LogCatAspect();
    }

}