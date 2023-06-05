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
public class EnumCheckerValidatorForString implements ConstraintValidator<EnumChecker, String> {

    private Class<? extends Enum> enumClass;

    @Override
    public void initialize(EnumChecker parameters) {
        enumClass = parameters.enumClass();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if(!StringUtils.hasText(value)) {
            return true;
        }

        return EnumUtils.anyMatch(enumClass, value);
    }
}