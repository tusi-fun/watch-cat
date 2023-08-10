package fun.tusi.sign.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import fun.tusi.sign.service.SignatureCatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static fun.tusi.sign.util.ApiSignUtils.buildSignPlaintext;

/**
 * 签名工具（适用于散列算法）
 * @author xy783
 */
@Slf4j
public class ApiSignUtils4Sha {

    public static final String
            APPID_KEY = "appid",
            SECRET_KEY = "secret",
            PATH_KEY = "path",
            CONTENT_MD5_KEY = "content-md5",
            METHOD_KEY = "method",
            TIMESTAMP_KEY = "timestamp",
            NONCE_KEY = "nonce",
            SIGN_KEY = "sign";

    /**
     * 签名
     * @param appid  签名appid
     * @param secret 签名密钥
     * @param algorithm 签名算法
     * @param method 请求方法
     * @param path   业务地址
     * @param requestParams 请求参数（form + path 中的参数）
     * @return
     */
    public static Map<String, String> sign(String appid, String secret, HmacAlgorithm algorithm, String method, String path, Map<String, String> requestParams) {

        log.info("ApiSignUtils4Sha > sign > 入参：appid=" + appid + ", secret=" + secret + ", algorithm=" + algorithm + ", method=" + method + ", path=" + path + ", requestParams=" + requestParams);

        String timestamp = String.valueOf(System.currentTimeMillis()/1000L),
               nonce = RandomUtil.randomString(16);

        // 构建签名体
        Map<String,String> signBody = requestParams!=null ? new HashMap(requestParams) : new HashMap();
        signBody.put(APPID_KEY, appid);
        signBody.put(METHOD_KEY, method);
        signBody.put(PATH_KEY, path);
        signBody.put(TIMESTAMP_KEY, timestamp);
        signBody.put(NONCE_KEY, nonce);

        // 验证签名
        HMac mac = new HMac(algorithm, secret.getBytes());

        // 构建签名返回
        Map<String,String> signResult = new HashMap();
        signResult.put(APPID_KEY, appid);
        signResult.put(NONCE_KEY, nonce);
        signResult.put(TIMESTAMP_KEY, timestamp);
        signResult.put(SIGN_KEY, mac.digestHex(buildSignPlaintext(signBody)));

        return signResult;
    }

	/**
	 * 校验签名
     * @param secret    签名密钥
	 * @param algorithm 签名算法
     * @param params    业务参数
	 * @param sign      签名值
	 * @param nonce     随机数
	 * @param timestamp unix 时间戳
	 * @return
     */
    public static boolean verify(String secret, HmacAlgorithm algorithm, String sign, String nonce, String timestamp, Map<String, String> params) {

        log.info("ApiSignUtils4Sha > verify > 入参：secret=" + secret + ", algorithm=" + algorithm + ", params=" + params + ", sign=" + sign + ", nonce=" + nonce + ", timestamp=" + timestamp);

        if(algorithm==null) {
            throw new SignatureCatException("验证签名：未找到签名算法为"+algorithm+"的配置");
        }

        if(!StringUtils.hasText(sign)) {
            throw new SignatureCatException("验证签名："+ SIGN_KEY +"不合法");
        }

        if(!StringUtils.hasText(nonce)) {
            throw new SignatureCatException("验证签名："+ NONCE_KEY +"不合法");
        }

        Map<String, String> signBody = new HashMap(params);
        signBody.put(NONCE_KEY,nonce);
        signBody.put(TIMESTAMP_KEY,timestamp);

        // 验证签名
        HMac mac = new HMac(algorithm, secret.getBytes());

        String _sign = mac.digestHex(buildSignPlaintext(signBody));

        log.info("ApiSignUtils4Sha > 签名结果 = {}", _sign);

        return sign.equals(_sign);
    }


}