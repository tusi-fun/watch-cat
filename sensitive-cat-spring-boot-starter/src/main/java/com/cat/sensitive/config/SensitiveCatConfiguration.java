package com.cat.sensitive.config;

import com.cat.sensitive.aspect.SensitiveAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * watch-cat 配置
 * @author hudongshan
 * @version 2021/10/14
 *
 * - @ConditionalOnMissingBean
 * 修饰bean的注解，主要实现当bean被注册之后，再注册相同类型的bean，就不会成功，
 * 它会保证你的bean只有一个，即你的实例只有一个，当你注册多个相同的bean时，会出现异常
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