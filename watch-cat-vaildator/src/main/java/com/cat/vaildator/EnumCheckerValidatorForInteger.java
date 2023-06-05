package com.cat.vaildator;

import com.cat.utils.EnumUtils;
import com.cat.utils.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 自定义参数校验器（验证业务状态、取值范围等）
 * @author hudongshan
 * @version 20210831
 */
public class EnumCheckerValidatorForInteger implements ConstraintValidator<EnumChecker, Integer> {

    private Class<? extends Enum> enumClass;
    private String enumField;

    @Override
    public void initialize(EnumChecker parameters) {
        enumClass = parameters.enumClass();
        enumField = parameters.enumField();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        if(value==null || !StringUtils.hasText(enumField)) {
            return true;
        }

        return EnumUtils.isValidEnumValue(enumClass, enumField, value);

    }
}