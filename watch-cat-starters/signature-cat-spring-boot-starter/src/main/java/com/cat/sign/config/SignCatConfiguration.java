package com.cat.sign.config;

import com.cat.sign.aspect.SignCatAspect;
import com.cat.sign.service.SignCommonService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
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
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({
        SignProperties.class
})
public class SignCatConfiguration {

    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "watchcat.sign", name = "enabled", havingValue = "true")
    @Bean
    public SignCatAspect signCatAspect() {
        log.info("Initializing {} > {}", this.getClass().getSimpleName(), "signCatAspect");
        return new SignCatAspect();
    }

    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "watchcat.sign", name = "enabled", havingValue = "true")
    @Bean
    public SignCommonService cacheService(RedisTemplate redisTemplate) {
        log.info("Initializing {} > {}", this.getClass().getSimpleName(), "cacheService");
        return new SignCommonService(redisTemplate);
    }

    /**
     * @ConditionalOnMissingBean 和 @ConditionalOnProperty 同时使用，为 AND 关系
     * @param factory
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "watchcat.sign", name = "enabled", havingValue = "true")
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