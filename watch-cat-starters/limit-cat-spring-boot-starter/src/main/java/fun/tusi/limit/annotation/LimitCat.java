package fun.tusi.limit.annotation;

import java.lang.annotation.*;

/**
 * 业务频率注解
 * @author xy783
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(LimitCats.class)
public @interface LimitCat {

    /**
     * 频率场景，默认为方法名
     * @return
     */
    String scene() default "";

    /**
     * 频率 key（支持SpEL）
     * @return
     */
    String key() default "";

    /**
     * 指定的 RuntimeException 下执行频率计数
     * @return
     */
    Class<? extends RuntimeException>[] triggerFor() default {};

    /**
     * 异常标记 字段（默认为code）
     * @return
     */
    String triggerForCodeField() default "code";

    /**
     * 指定的 Exception 且 异常标记（code、errcode、status等）满足等情况下执行频率计数
     * @return
     */
    String[] triggerForCode() default {};

    /**
     * 频率规则（使用代码指定，优先级最高）
     * @return
     */
    LimitCatRule[] rules() default {};
}
