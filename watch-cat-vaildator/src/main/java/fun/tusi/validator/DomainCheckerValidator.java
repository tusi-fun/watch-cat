package fun.tusi.validator;

import fun.tusi.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 自定义参数校验器（验证业务状态、取值范围等）
 * @author xy783
 * @version 20210831
 */
public class DomainCheckerValidator implements ConstraintValidator<DomainChecker, String> {

    private static final Pattern LOCAL_PART_PATTERN = Pattern.compile("^[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?$");

    @Override
    public void initialize(DomainChecker parameters) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if(!StringUtils.hasText(value)) {
            return true;
        }

        return LOCAL_PART_PATTERN.matcher(value).matches();
    }

//    public static void main(String[] args) {
//        System.out.println(LOCAL_PART_PATTERN.matcher("www").matches());
//        System.out.println(LOCAL_PART_PATTERN.matcher("http://www").matches());
//        System.out.println(LOCAL_PART_PATTERN.matcher("http://www.baidu.com").matches());
//        System.out.println(LOCAL_PART_PATTERN.matcher("https://www.baidu.com").matches());
//        System.out.println(LOCAL_PART_PATTERN.matcher("www.baidu.com").matches());
//        System.out.println(LOCAL_PART_PATTERN.matcher("a.com").matches());
//    }

}