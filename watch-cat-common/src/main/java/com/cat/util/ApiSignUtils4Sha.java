package com.cat.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import com.cat.enumerate.ApiSignKeyEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 签名工具（适用于散列算法）
 * @author hudongshan
 * @version 20200929
 */
@Slf4j
public class ApiSignUtils4Sha {

    private static final String APPID_KEY = ApiSignKeyEnum.APPID_KEY.value;

    private static final String PATH_KEY = ApiSignKeyEnum.PATH_KEY.value;
    private static final String METHOD_KEY = ApiSignKeyEnum.METHOD_KEY.value;
    private static final String NONCE_KEY = ApiSignKeyEnum.NONCE_KEY.value;
    private static final String TIMESTAMP_KEY = ApiSignKeyEnum.TIMESTAMP_KEY.value;
    private static final String SIGN_KEY = ApiSignKeyEnum.SIGN_KEY.value;
    
    /**
     * 签名
     * @param appid  签名appid
     * @param secret 签名密钥
     * @param requestParams 请求参数（form + path 中的参数）
     * @param method 请求方法
     * @param path   业务地址
     * @return
     */
    public static Map<String, String> sign(String appid,
                                    String secret,
                                    HmacAlgorithm algorithm,
                                    String method,
                                    String path,
                                    Map<String, String> requestParams) {

        log.info("计算签名：appid = " + appid + ", secret = " + secret + ", method = " + method + ", path = " + path + ", requestParams = " + requestParams);

        String timestamp = String.valueOf(System.currentTimeMillis()/1000L);
        String nonce = RandomUtil.randomString(16);

        // 构建签名体
        Map<String,String> signBody = requestParams!=null ? Maps.newHashMap(requestParams) : Maps.newHashMap();
        signBody.put(APPID_KEY, appid);
        signBody.put(METHOD_KEY, method);
        signBody.put(PATH_KEY, path);
        signBody.put(TIMESTAMP_KEY, timestamp);
        signBody.put(NONCE_KEY, nonce);

        // 构建签名返回
        Map<String,String> signResult = Maps.newHashMap();
        signResult.put(APPID_KEY, appid);
        signResult.put(TIMESTAMP_KEY, timestamp);
        signResult.put(NONCE_KEY, nonce);
        signResult.put(SIGN_KEY, genSign(secret,algorithm,signBody));

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
    public static boolean verify(String secret, HmacAlgorithm algorithm, Map<String, String> params, String sign, String nonce, String timestamp) {

        log.info("验证签名：secret=" + secret + ",algorithm=" + algorithm + ",params=" + params + ",sign=" + sign + ",nonce=" + nonce + ",timestamp=" + timestamp);

        Assert.isTrue(StringUtils.hasText(sign),"验证签名："+ SIGN_KEY+"不合法");
        Assert.isTrue(StringUtils.hasText(nonce),"验证签名："+ NONCE_KEY+"不合法");

        log.info("验证签名：构建签名体");

        Map<String, String> signBody = Maps.newHashMap(params);
        signBody.put(NONCE_KEY,nonce);
        signBody.put(TIMESTAMP_KEY,timestamp);

        // 验证签名
        String _sign = genSign(secret, algorithm, signBody);

        log.info("验证签名：计算{}={}", SIGN_KEY, _sign);

        return sign.equals(_sign);
    }


    /**
     * 生成 Sign 值 (排序、去空值、计算签名)
     * @param secret
     * @param signBody （包含业务参数、nonce、timestamp、accept、content-type）
     */
    private static String genSign(String secret, HmacAlgorithm algorithm, Map<String,String> signBody) {

        log.info("计算签名：secret="+secret+",algorithm="+algorithm+",signBody="+signBody);

        Assert.notNull(algorithm,"计算签名：未找到签名算法为"+algorithm+"的配置");

        HMac mac = new HMac(algorithm,secret.getBytes());

        return mac.digestHex(buildSignBody(signBody));
    }

    private static String buildSignBody(Map<String,String> signBody) {

        // 移除签名体中已有的 sign 参数（如果存在）
        signBody.remove(SIGN_KEY);

        // 获取 signBody 中的 key 集合
        List<String> keyList = Lists.newArrayList(signBody.keySet());

        // 排序
        Collections.sort(keyList);

        // 构建签名内容
        StringBuilder sb = new StringBuilder();

        for (String key : keyList) {
            String value = signBody.get(key);
            if(StringUtils.hasText(value)) {
                sb.append(key).append(value);
            }
        }

        log.info("构建签名体：签名原文 = {}",sb);

        return sb.toString();

    }

}