package com.cat;

import com.cat.converter.String2LocalDateConverter;
import com.cat.converter.String2LocalDateTimeConverter;
import com.cat.converter.String2LocalTimeConverter;
import com.cat.converter.StringTrimConverter;
import com.cat.converter.area.AreaDetailConverter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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
public class WatchCatConfiguration{

    /**
     * WatchCat RedisTemplate 配置
     * 20221208 不能使用 @ConditionalOnMissingBean 注解修饰，使用者的项目中很可能存在自定义 RedisTemplate
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> wcRedisTemplate(RedisConnectionFactory factory) {

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

    /**
     * Tips：使用 @Component 方式注册，可能会因为 package 不一样，导致 Convert 无法被 Spring 自动扫描到
     * @return
     */
    @ConditionalOnWebApplication
    @Bean
    public AreaDetailConverter areaDetailConverter(){
        return new AreaDetailConverter();
    }

    /**
     * Tips：使用 @Component 方式注册，可能会因为 package 不一样，导致 Convert 无法被 Spring 自动扫描到
     * @return
     */
    @ConditionalOnWebApplication
    @Bean
    public StringTrimConverter stringTrimConverter(){
        return new StringTrimConverter();
    }

    /**
     * Tips：使用 @Component 方式注册，可能会因为 package 不一样，导致 Convert 无法被 Spring 自动扫描到
     * @return
     */
    @ConditionalOnWebApplication
    @Bean
    public String2LocalDateConverter string2LocalDateConverter(){
        return new String2LocalDateConverter();
    }

    /**
     * Tips：使用 @Component 方式注册，可能会因为 package 不一样，导致 Convert 无法被 Spring 自动扫描到
     * @return
     */
    @ConditionalOnWebApplication
    @Bean
    public String2LocalDateTimeConverter string2LocalDateTimeConverter(){
        return new String2LocalDateTimeConverter();
    }

    /**
     * Tips：使用 @Component 方式注册，可能会因为 package 不一样，导致 Convert 无法被 Spring 自动扫描到
     * @return
     */
    @ConditionalOnWebApplication
    @Bean
    public String2LocalTimeConverter string2LocalTimeConverter(){
        return new String2LocalTimeConverter();
    }

}