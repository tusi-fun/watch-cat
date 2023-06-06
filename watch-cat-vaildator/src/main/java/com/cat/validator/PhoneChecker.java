package com.cat.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * 自定义参数校验器（验证参数值是否为手机号格式）
 * @author hudongshan
 * @version 20210831
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(PhoneChecker.List.class)
@Documented
@Constraint(validatedBy = {PhoneCheckerValidatorForString.class})
public @interface PhoneChecker {

    /**
     * 使用示例：
     * @NotBlank
     * @PhoneChecker
     * String phone;
     */

    String message() default "{com.cat.validator.PhoneChecker.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@link PhoneChecker} annotations on the same element.
     *
     * @see PhoneChecker
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        PhoneChecker[] value();
    }
}
