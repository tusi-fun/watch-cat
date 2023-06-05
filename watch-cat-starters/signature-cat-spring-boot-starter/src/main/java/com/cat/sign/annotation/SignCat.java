package com.cat.sign.annotation;

import java.lang.annotation.*;

/**
 * 请求验签，响应加签注解
 * @author hudongshan
 * @version 20221018
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SignCat {

	/**
	 * 是否启用签名验证
	 * @return
	 */
	boolean checkSign() default true;

	/**
	 * json 请求接收对象
	 *
	 * @return
	 */
	Class jsonTarget() default Object.class;

}