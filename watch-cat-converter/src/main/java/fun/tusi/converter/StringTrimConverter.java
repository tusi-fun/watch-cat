package fun.tusi.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

/**
 * String 类型请求参数，去除前后空格
 * @author xy783
 */
@Slf4j
public class StringTrimConverter implements Converter<String, String> {

	@Override
	public String convert(String source) {
		return source!=null?source.trim():null;
	}

}