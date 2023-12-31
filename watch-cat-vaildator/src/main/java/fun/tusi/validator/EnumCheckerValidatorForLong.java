package fun.tusi.validator;

import fun.tusi.util.EnumUtils;
import fun.tusi.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * 自定义参数校验器（验证业务状态、取值范围等）
 * @author xy783
 * @version 20210831
 */
public class EnumCheckerValidatorForLong implements ConstraintValidator<EnumChecker, Long> {

    private Class<? extends Enum> enumClass;
    private String enumField;

    @Override
    public void initialize(EnumChecker parameters) {
        enumClass = parameters.enumClass();
        enumField = parameters.enumField();
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {

        if(value==null || !StringUtils.hasText(enumField)) {
            return true;
        }

        return EnumUtils.isValidEnumValue(enumClass, enumField, value);
    }

}