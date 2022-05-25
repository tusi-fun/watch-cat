package com.cat.watchcat;


import com.cat.watchcat.converter.*;
import com.cat.watchcat.limit.aspect.LimitCatsAspect;
import com.cat.watchcat.limit.config.LimitCatProperties;
import com.cat.watchcat.limit.service.LimitCatService;
import com.cat.watchcat.log.aspect.LogCatAspect;
import com.cat.watchcat.secret.aspect.SecretCatAspect;
//import com.cat.watchcat.secret.aspect.VictoriasSecretAspect;
import com.cat.watchcat.secret.config.SecretCatProperties;
import com.cat.watchcat.secret.service.DataEncryptService;
import com.cat.watchcat.sensitive.aspect.SensitiveAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * watch-cat 配置
 * @author hudongshan
 * @version 2021/10/14
 *
 * @ConditionalOnMissingBean，它是修饰bean的一个注解，主要实现的是，当bean被注册之后，再注册相同类型的bean，就不会成功，它会保证你的bean只有一个，即你的实例只有一个，当你注册多个相同的bean时，会出现异常
 */
@Configuration
@EnableConfigurationProperties({LimitCatProperties.class, SecretCatProperties.class})
public class WatchCatConfiguration implements WebMvcConfigurer {

    @ConditionalOnMissingBean
    @Bean
    public LimitCatsAspect limitCatsAspect(){
        return new LimitCatsAspect();
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

//    @ConditionalOnMissingBean
//    @Bean
//    public VictoriasSecretAspect victoriasSecretAspect(){
//        return new VictoriasSecretAspect();
//    }

    @ConditionalOnMissingBean
    @Bean
    public DataEncryptService dataEncryptService(){
        return new DataEncryptService();
    }

    @ConditionalOnMissingBean
    @Bean
    public LimitCatService limitCatService(){
        return new LimitCatService();
    }

    @ConditionalOnMissingBean
    @Bean
    public AreaDetailConverter areaDetailConverter(){
        return new AreaDetailConverter();
    }

    /**
     * 注册自定义 convert
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(areaDetailConverter());
        registry.addConverter(new StringTrimConverter());
        registry.addConverter(new String2LocalDateConverter());
        registry.addConverter(new String2LocalDateTimeConverter());
        registry.addConverter(new String2LocalTimeConverter());
    }
}