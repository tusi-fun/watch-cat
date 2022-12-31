package com.cat.watchcat.log.annotation;

import java.lang.annotation.*;

/**
 * 日志记录注解
 * @author hudongshan
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LogCat {

    /**
     * 自定义参数（支持 SpEL）
     * @return
     */
    String bid() default "";

    /**
     * 操作分组
     * @return
     */
    String actionGroup() default "default";

    /**
     * 操作
     * @return
     */
    String action() default "default";

    /**
     * 是否启用日志事件通知（用于持久化日志等）
     * @return
     */
    boolean enableEvent() default true;

    /**
     * 是否启用日志打印
     * @return
     */
    boolean print() default true;

}