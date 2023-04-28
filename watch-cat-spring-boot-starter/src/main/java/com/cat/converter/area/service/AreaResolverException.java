package com.cat.converter.area.service;


import java.io.Serializable;

/**
 * 地区参数解析异常
 * @author hudongshan
 * @version 2021/12/9
 */
public class AreaResolverException extends RuntimeException implements Serializable {

	private static final long serialVersionUID = -5297058725206206709L;

	public AreaResolverException(String message) {
		super(message);
	}
}
