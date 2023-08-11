package fun.tusi.sign.annotation;

import java.lang.annotation.*;

/**
 * 请求验签，响应加签注解
 * @author xy783
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SignatureCat {

	/**
	 * json 请求接收对象
	 *
	 * @return
	 */
	Class jsonTarget() default Object.class;

}