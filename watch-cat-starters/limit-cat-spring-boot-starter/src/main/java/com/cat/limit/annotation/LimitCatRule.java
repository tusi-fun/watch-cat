package com.cat.limit.annotation;

import java.lang.annotation.*;

/**
 * 业务频率规则注解
 * @author xy783
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LimitCatRule {

    /**
     * 周期（秒）
     * @return
     */
    long interval();

    /**
     * 允许执行次数
     * @return
     */
    long frequency();

    /**
     * 频率超限提示
     * @return
     */
    String message() default "";
}
