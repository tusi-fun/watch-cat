package com.cat.watchcat.sensitive.annotation;

import cn.hutool.core.util.DesensitizedUtil;

import java.lang.annotation.*;

/**
 * 敏感字段标记注解
 * @author yangxiujun
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SensitiveField {

    /**
    * 脱敏类型(规则)
    */
    DesensitizedUtil.DesensitizedType type();

}