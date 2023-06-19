package com.cat.sign.service;

/**
 * 验签 AppSecret 接口
 * @author xy783
 */
public interface AppService {

	/**
	 * 使用 appId 获取 appSecret （用于签名验证）
	 * @param appId
	 * @return
	 */
	String getAppSecret(String appId);

}