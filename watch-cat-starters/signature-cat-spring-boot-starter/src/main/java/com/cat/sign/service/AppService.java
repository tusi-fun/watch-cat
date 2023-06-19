package com.cat.sign.service;

/**
 * @author xy783
 * @version 2021/12/9
 */
public interface AppService {

	/**
	 * 使用 appId 获取 appSecret （用于签名验证）
	 * @param appId
	 * @return
	 */
	String getAppSecret(String appId);

}