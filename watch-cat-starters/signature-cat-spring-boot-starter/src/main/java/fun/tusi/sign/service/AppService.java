package fun.tusi.sign.service;

/**
 * 获取 AppSecret 接口
 * @author xy783
 */
public interface AppService {

	/**
	 * 使用 appid 获取 appSecret （用于签名验证）
	 * @param appid
	 * @return
	 */
	String getAppSecret(String appid);

}