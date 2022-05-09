package com.cat.watchcat.sensitive.annotation;

import java.lang.annotation.*;

/**
 * api 响应脱敏标记注解
 * @author yangxiujun
 */
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SensitiveCat {

    /**
     * 响应中业务参数对象
     * @return
     */
    String field() default "data";

}