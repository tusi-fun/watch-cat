package fun.tusi.validator;


import fun.tusi.utils.EnumUtils;
import fun.tusi.utils.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * 自定义参数校验器（验证业务状态、取值范围等）
 * @author xy783
 * @version 20210831
 */
public class EnumCheckerValidatorForString implements ConstraintValidator<EnumChecker, String> {

    private Class<? extends Enum> enumClass;
    private String enumField;

    @Override
    public void initialize(EnumChecker parameters) {
        enumClass = parameters.enumClass();
        enumField = parameters.enumField();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if(!StringUtils.hasText(value)) {
            return true;
        }

        // 使用 String 接收参数，可以比较枚举 name 或 指定字段比较。
        // Long 或 Integer 类型则只能指定字段比较，因为枚举 name() 本身就为 String 类型
        if(StringUtils.hasText(enumField)) {
            return EnumUtils.isValidEnumValue(enumClass, enumField, value);
        } else {
            return EnumUtils.anyMatch(enumClass, value);
        }


    }
}