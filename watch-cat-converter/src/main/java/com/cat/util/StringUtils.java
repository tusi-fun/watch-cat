package com.cat.util;

/**
 * 复制于 SpringBoot 自带 StringUtil
 * @author hudongshan
 * @version 2022/5/12
 */

public abstract class StringUtils {

	public StringUtils() {}

	public static boolean isEmpty(Object str) {
		return str == null || "".equals(str);
	}

	public static boolean hasText(CharSequence str) {
		return str != null && str.length() > 0 && containsText(str);
	}

	public static boolean hasText(String str) {
		return str != null && !str.isEmpty() && containsText(str);
	}

	private static boolean containsText(CharSequence str) {
		int strLen = str.length();

		for(int i = 0; i < strLen; ++i) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}

		return false;
	}

}
