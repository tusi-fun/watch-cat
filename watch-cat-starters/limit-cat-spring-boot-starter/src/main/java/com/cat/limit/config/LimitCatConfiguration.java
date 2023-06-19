package com.cat.limit.config;

import com.cat.limit.aspect.LimitCatsAspect;
import com.cat.limit.service.LimitCatService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * watch-cat 配置
 * @author xy783
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

        log.info("Initializing {} > {}", this.getClass().getSimpleName(), "limitCatsAspect");

        return new LimitCatsAspect();
    }

    @Bean
    @ConditionalOnProperty(prefix = "watchcat.limit", name = "enabled", havingValue = "true")
    public LimitCatService limitCatService(RedisTemplate redisTemplate, LimitCatProperties limitCatProperties) {

        log.info("Initializing {} > {}", this.getClass().getSimpleName(), "limitCatService");

        return new LimitCatService(redisTemplate, limitCatProperties);
    }

    /**
     * @ConditionalOnMissingBean 和 @ConditionalOnProperty 同时使用，为 AND 关系
     * @param factory
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "watchcat.limit", name = "enabled", havingValue = "true")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        log.info("Initializing {} > {}", this.getClass().getSimpleName(), "redisTemplate");

        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);

        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        RedisSerializer<String> stringSerializer = new StringRedisSerializer();

        RedisTemplate<String, Object> wcRedisTemplate = new RedisTemplate<>();
        wcRedisTemplate.setConnectionFactory(factory);
        wcRedisTemplate.setKeySerializer(stringSerializer);
        wcRedisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        wcRedisTemplate.setHashKeySerializer(stringSerializer);
        wcRedisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        wcRedisTemplate.afterPropertiesSet();

        return wcRedisTemplate;
    }


}