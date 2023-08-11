package fun.tusi.sign.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 接口加签、验签工具（适用于散列算法）
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

        log.info("[签名][appid=" + appid + ", secret=" + secret + ", algorithm=" + algorithm + ", method=" + method + ", path=" + path + ", requestParams=" + requestParams+"]");

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

        String signPlaintext = buildSignPlaintext(signBody);
        log.info("[签名]signPlaintext = {}", signPlaintext);

        signResult.put(SIGN_KEY, mac.digestHex(signPlaintext));

        return signResult;
    }

    /**
     * 验签
     * @param secret    签名密钥
     * @param algorithm 签名算法
     * @param params    业务参数 （已经包含 nonce、timestamp 等签名元参数）
     * @param outSign   签名值
     * @return
     */
    public static boolean verify(String secret, HmacAlgorithm algorithm, String outSign, Map<String, String> params) {

        log.info("[验签][secret=" + secret + ", algorithm=" + algorithm + ", outSign=" + outSign + ", params=" + params +"]");

        Map<String, String> signBody = new HashMap(params);

        // 验证签名
        HMac mac = new HMac(algorithm, secret.getBytes());

        String signPlaintext = buildSignPlaintext(signBody);

        log.info("[验签]signPlaintext = {}", signPlaintext);

        String _sign = mac.digestHex(signPlaintext);

        log.info("[验签]outSign = {}", outSign);
        log.info("[验签]sign = {}", _sign);

        return outSign.equals(_sign);
    }

//	/**
//	 * 校验签名
//     * @param secret    签名密钥
//	 * @param algorithm 签名算法
//     * @param params    业务参数
//	 * @param outSign   签名值
//	 * @param nonce     随机数
//	 * @param timestamp unix 时间戳
//	 * @return
//     */
//    public static boolean verify(String secret, HmacAlgorithm algorithm, String outSign, String nonce, String timestamp, Map<String, String> params) {
//
//        log.info("> verify[secret=" + secret + ", algorithm=" + algorithm + ", params=" + params + ", outSign=" + outSign + ", nonce=" + nonce + ", timestamp=" + timestamp+"]");
//
//        Map<String, String> signBody = new HashMap(params);
//        signBody.put(NONCE_KEY,nonce);
//        signBody.put(TIMESTAMP_KEY,timestamp);
//
//        // 验证签名
//        HMac mac = new HMac(algorithm, secret.getBytes());
//
//        String _sign = mac.digestHex(buildSignPlaintext(signBody));
//
//        log.info("> outSign = {}", outSign);
//        log.info(">    sign = {}", _sign);
//
//        return outSign.equals(_sign);
//    }

    /**
     * 构建签名原文
     * @param signBodyMap
     * @return signBody
     */
    public static String buildSignPlaintext(Map<String,String> signBodyMap) {

        // 移除参与签名参数中已有的 sign 参数（如果存在）
        signBodyMap.remove(SIGN_KEY);

        // 获取 signBody 中的 key 集合
        List<String> keyList = new ArrayList(signBodyMap.keySet());

        // 排序
        Collections.sort(keyList);

        // 构建签名内容
        StringBuilder sb = new StringBuilder();

        for (String key : keyList) {

            String value = signBodyMap.get(key);

            // 移除空値
            if(StringUtils.hasText(value)) {

                sb.append(key).append(value);
            }
        }

        return sb.toString();
    }

}