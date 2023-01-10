package com.cat.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * 自定义参数校验器（验证参数值是否为域名格式，不包含协议头）
 * www.xx.com
 * @author hudongshan
 * @version 20210831
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(DomainChecker.List.class)
@Documented
@Constraint(validatedBy = {DomainCheckerValidator.class})
public @interface DomainChecker {

    /**
     * 使用示例：
     *
     * @NotBlank
     * @DomainChecker
     * String domain;
     */

    String message() default "{com.xx.validator.DomainChecker.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@link DomainChecker} annotations on the same element.
     *
     * @see DomainChecker
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        DomainChecker[] value();
    }
}
