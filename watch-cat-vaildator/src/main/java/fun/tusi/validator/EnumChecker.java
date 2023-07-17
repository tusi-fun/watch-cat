package fun.tusi.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * 自定义参数校验器（验证参数值是否存在于指定枚举类型中）
 * @author xy783
 * @version 20210831
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(EnumChecker.List.class)
@Documented
@Constraint(validatedBy = {EnumCheckerValidatorForInteger.class, EnumCheckerValidatorForLong.class, EnumCheckerValidatorForString.class})
public @interface EnumChecker {

    /**
     * 使用示例：
     *
     * 1、判断枚举字段值是否包含
     * @NotNull
     * @EnumChecker(enumClass = GenderEnum.class, enumField = "code")
     * Integer gender;
     *
     * 2、判断枚举名是否包含
     * @NotBlank
     * @EnumChecker(enumClass = GenderEnum.class)
     * String genderEnum;
     */

    String message() default "{com.cat.validator.EnumChecker.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return date must in this value array
     */
    Class<? extends Enum<?>> enumClass();

    String enumField() default "";

    /**
     * Defines several {@link EnumChecker} annotations on the same element.
     *
     * @see EnumChecker
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        EnumChecker[] value();
    }
}
