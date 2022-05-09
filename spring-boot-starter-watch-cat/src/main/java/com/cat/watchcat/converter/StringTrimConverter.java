package com.cat.watchcat.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * String 类型请求参数，去除前后空格
 * @author hudongshan
 * @version 20220420
 */
@Slf4j
@Component
public class StringTrimConverter implements Converter<String, String> {

	@Override
	public String convert(String source) {

		log.info("StringTrimConverter->{}",source);

		return source!=null?source.trim():null;
	}

}