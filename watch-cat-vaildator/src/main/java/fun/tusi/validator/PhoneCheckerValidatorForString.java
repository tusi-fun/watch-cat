package fun.tusi.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * 自定义参数校验器（验证参数值是否为手机号格式）
 * @author xy783
 * @version 20210831
 */
public class PhoneCheckerValidatorForString implements ConstraintValidator<PhoneChecker, String> {

    private static final Pattern LOCAL_PART_PATTERN = Pattern.compile("^1\\d{10}$");

    @Override
    public void initialize(PhoneChecker parameters) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if(value==null) {
            return true;
        }

        return LOCAL_PART_PATTERN.matcher(value).matches();
    }
}