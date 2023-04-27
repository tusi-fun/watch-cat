package com.cat.watchcat.limit.config;

import com.cat.watchcat.limit.aspect.LimitCatsAspect;
import com.cat.watchcat.limit.service.LimitCatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@Slf4j
@Configuration
@EnableConfigurationProperties({
        LimitCatProperties.class
})
public class LimitCatConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "watchcat.limit", name = "enabled", havingValue = "true")
    public LimitCatsAspect limitCatsAspect() {
        return new LimitCatsAspect();
    }

    @Bean
    @ConditionalOnProperty(prefix = "watchcat.limit", name = "enabled", havingValue = "true")
    public LimitCatService limitCatService(RedisTemplate wcRedisTemplate, LimitCatProperties limitCatProperties){
        return new LimitCatService(wcRedisTemplate, limitCatProperties);
    }


}