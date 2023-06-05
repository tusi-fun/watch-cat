package com.cat.utils;

/**
 * RSA 公私钥格式化
 * @author hudongshan
 * @version 2022/9/27
 */
public class RsaKeyUtils {

	private static final String PRIVATE_KEY_BEGIN = "-----BEGIN RSA PRIVATE KEY-----";
	private static final String PRIVATE_KEY_END = "-----END RSA PRIVATE KEY-----";

	private static final String PUBLIC_KEY_BEGIN = "-----BEGIN PUBLIC KEY-----";
	private static final String PUBLIC_KEY_END = "-----END PUBLIC KEY-----";

	/**
	 * 移除公私钥前后分隔符以及换行符、回车符、前后空格
	 * @param original
	 * @return
	 */
	public static String cleanToSingleLine(String original) {

		if(!StringUtils.hasText(original)) {
			return "";
		}

		return original.replace(PUBLIC_KEY_BEGIN, "")
				.replace(PUBLIC_KEY_END, "")
				.replace(PRIVATE_KEY_BEGIN, "")
				.replace(PRIVATE_KEY_END, "")
				.replaceAll("\\n", "")
				.replaceAll("\\r", "").trim();

	}
}
