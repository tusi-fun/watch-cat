package com.cat.sign.config;

import com.cat.sign.aspect.SignCatAspect;
import com.cat.sign.service.SignCommonService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

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
        SignProperties.class
})
public class SignCatConfiguration {

    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "watchcat.sign", name = "enabled", havingValue = "true")
    @Bean
    public SignCatAspect signCatAspect() {
        return new SignCatAspect();
    }

    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "watchcat.sign", name = "enabled", havingValue = "true")
    @Bean
    public SignCommonService cacheService(RedisTemplate wcRedisTemplate) {
        return new SignCommonService(wcRedisTemplate);
    }

}