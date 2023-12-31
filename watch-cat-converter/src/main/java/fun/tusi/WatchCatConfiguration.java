package fun.tusi;

import fun.tusi.converter.String2LocalDateConverter;
import fun.tusi.converter.String2LocalDateTimeConverter;
import fun.tusi.converter.String2LocalTimeConverter;
import fun.tusi.converter.StringTrimConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
public class WatchCatConfiguration {

    /**
     * Tips：使用 @Component 方式注册，可能会因为 package 不一样，导致 Convert 无法被 Spring 自动扫描到
     * @return
     */
    @ConditionalOnWebApplication
    @Bean
    public StringTrimConverter stringTrimConverter() {

        log.info("Initializing {} > {}", this.getClass().getSimpleName(), "stringTrimConverter");

        return new StringTrimConverter();
    }

    /**
     * Tips：使用 @Component 方式注册，可能会因为 package 不一样，导致 Convert 无法被 Spring 自动扫描到
     * @return
     */
    @ConditionalOnWebApplication
    @Bean
    public String2LocalDateConverter string2LocalDateConverter() {

        log.info("Initializing {} > {}", this.getClass().getSimpleName(), "string2LocalDateConverter");

        return new String2LocalDateConverter();
    }

    /**
     * Tips：使用 @Component 方式注册，可能会因为 package 不一样，导致 Convert 无法被 Spring 自动扫描到
     * @return
     */
    @ConditionalOnWebApplication
    @Bean
    public String2LocalDateTimeConverter string2LocalDateTimeConverter() {

        log.info("Initializing {} > {}", this.getClass().getSimpleName(), "string2LocalDateTimeConverter");

        return new String2LocalDateTimeConverter();
    }

    /**
     * Tips：使用 @Component 方式注册，可能会因为 package 不一样，导致 Convert 无法被 Spring 自动扫描到
     * @return
     */
    @ConditionalOnWebApplication
    @Bean
    public String2LocalTimeConverter string2LocalTimeConverter() {

        log.info("Initializing {} > {}", this.getClass().getSimpleName(), "string2LocalTimeConverter");

        return new String2LocalTimeConverter();
    }

}