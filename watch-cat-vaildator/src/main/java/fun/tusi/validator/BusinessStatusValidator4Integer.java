package fun.tusi.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

/**
 * 自定义参数校验器（验证业务状态、取值范围等）
 * @author xy783
 * @version 20210831
 */
public class BusinessStatusValidator4Integer implements ConstraintValidator<BusinessStatus, Integer> {

    private int[] businessStatusList;

    @Override
    public void initialize(BusinessStatus parameters) {
        businessStatusList = parameters.value();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if(value == null) {
            return true;
        }
        return Arrays.stream(businessStatusList).anyMatch(value::equals);
    }
}