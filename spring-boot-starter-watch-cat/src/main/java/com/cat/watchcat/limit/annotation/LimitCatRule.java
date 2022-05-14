package com.cat.watchcat.limit.annotation;

import java.lang.annotation.*;

/**
 * 业务频率规则注解
 * @author hudongshan
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LimitCatRule {

    /**
     * 时间间隔（秒）
     * @return
     */
    long intervalSeconds();

    /**
     * 允许执行次数
     * @return
     */
    long frequency();

    /**
     * 频率超限提示
     * @return
     */
    String msg() default "";
}
