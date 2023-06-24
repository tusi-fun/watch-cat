package com.cat.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * 自定义参数校验器（验证业务状态、取值范围等）
 * @author xy783
 * @version 20210831
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(BusinessStatus.List.class)
@Documented
@Constraint(validatedBy = BusinessStatusValidator4Integer.class)
public @interface BusinessStatus {

    /**
     * 使用示例：
     * @NotNull
     * @BusinessStatus(value = {0,1})
     * Integer gender;
     */

    String message() default "{com.cat.validator.BusinessStatus.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return date must in this value array
     */
    int[] value();

    /**
     * Defines several {@link BusinessStatus} annotations on the same element.
     *
     * @see BusinessStatus
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        BusinessStatus[] value();
    }
}
