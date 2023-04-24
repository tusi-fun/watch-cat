package com.cat.watchcat;

import com.cat.watchcat.converter.area.AreaDetailConverter;
import com.cat.watchcat.limit.aspect.LimitCatsAspect;
import com.cat.watchcat.limit.config.LimitCatProperties;
import com.cat.watchcat.limit.service.LimitCatService;
import com.cat.watchcat.log.aspect.LogCatAspect;
import com.cat.watchcat.secret.aspect.SecretCatAspect;
import com.cat.watchcat.secret.config.SecretCatProperties;
import com.cat.watchcat.secret.service.DataEncryptService;
import com.cat.watchcat.sensitive.aspect.SensitiveAspect;
import com.cat.watchcat.sign.aspect.SignCatAspect;
import com.cat.watchcat.sign.config.SignShaProperties;
import com.cat.watchcat.sign.config.SignSymmetricProperties;
import com.cat.watchcat.sign.service.SignCommonService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
 * @author hudongshan
 * @version 2021/10/14
 *
 * - @ConditionalOnMissingBean
 * 修饰bean的注解，主要实现当bean被注册之后，再注册相同类型的bean，就不会成功，
 * 它会保证你的bean只有一个，即你的实例只有一个，当你注册多个相同的bean时，会出现异常
 */
@Configuration
@EnableConfigurationProperties({LimitCatProperties.class, SecretCatProperties.class, SignShaProperties.class, SignSymmetricProperties.class})
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

    @ConditionalOnMissingBean
    @Bean
    public LimitCatsAspect limitCatsAspect() {
        return new LimitCatsAspect();
    }

    @ConditionalOnMissingBean
    @Bean
    public LimitCatService limitCatService(RedisTemplate wcRedisTemplate,LimitCatProperties limitCatProperties){
        return new LimitCatService(wcRedisTemplate, limitCatProperties);
    }

    @ConditionalOnMissingBean
    @Bean
    public LogCatAspect logCatAspect(){
        return new LogCatAspect();
    }

    @ConditionalOnMissingBean
    @Bean
    public SensitiveAspect sensitiveAspect(){
        return new SensitiveAspect();
    }

    @ConditionalOnMissingBean
    @Bean
    public SecretCatAspect secretCatAspect(){
        return new SecretCatAspect();
    }

    @ConditionalOnMissingBean
    @Bean
    public DataEncryptService dataEncryptService(RedisTemplate wcRedisTemplate,SecretCatProperties secretCatProperties){
        return new DataEncryptService(wcRedisTemplate,secretCatProperties);
    }

    @ConditionalOnMissingBean
    @Bean
    public AreaDetailConverter areaDetailConverter(){
        return new AreaDetailConverter();
    }

    @ConditionalOnMissingBean
    @Bean
    public SignCatAspect signCatAspect(){
        return new SignCatAspect();
    }

    @ConditionalOnMissingBean
    @Bean
    public SignCommonService cacheService(RedisTemplate wcRedisTemplate){
        return new SignCommonService(wcRedisTemplate);
    }

}