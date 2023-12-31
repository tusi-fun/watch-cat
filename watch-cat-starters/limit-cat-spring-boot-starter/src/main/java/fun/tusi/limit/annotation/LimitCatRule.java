package fun.tusi.limit.annotation;

import java.lang.annotation.*;

/**
 * 频率限制规则（编码式配置规则）注解
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
