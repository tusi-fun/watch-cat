package com.cat.example.exception;

import java.io.Serializable;

/**
 * @author hudongshan
 * @version 2021/11/22
 */
public class LimitCatCase3Exception extends RuntimeException implements Serializable {

	private static final long serialVersionUID = -1041148006846583767L;

	private final int code;

	private String message;

	public LimitCatCase3Exception(Integer code, String message, String... args) {

		super(String.format(message, args));

		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

}
