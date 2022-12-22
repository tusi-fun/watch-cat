package com.cat.exception;

import java.io.Serializable;

/**
 * @author hudongshan
 * @version 2021/11/22
 */
public class LimitCatCase4Exception extends RuntimeException implements Serializable {

	private static final long serialVersionUID = -1041148006846583767L;

	private int status;

	private String message;

	public LimitCatCase4Exception(Integer status, String message, String... args) {

		super(String.format(message, args));

		this.status = status;
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

}
