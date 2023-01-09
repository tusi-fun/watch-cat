package com.cat.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 自定义参数校验器（验证业务状态、取值范围等）
 * @author hudongshan
 * @version 20210831
 */
public class DomainCheckerValidator implements ConstraintValidator<DomainChecker, String> {

    private static final Pattern LOCAL_PART_PATTERN = Pattern.compile("^[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?$");

    @Override
    public void initialize(DomainChecker parameters) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if(value==null) {

            return true;
        }

        return LOCAL_PART_PATTERN.matcher(value).matches();

    }

}