package com.cat.watchcat.sign.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import com.cat.util.SignUtils;
import com.cat.watchcat.sign.config.SignShaProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 签名工具（适用于散列算法）
 * @author hudongshan
 * @version 20200929
 */
@Slf4j
@Component
public class ApiSignUtils4Sha {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private SignShaProperties signShaProperties;

    private static final String PATH_KEY = SignKeyEnum.PATH_KEY.value;
    private static final String METHOD_KEY = SignKeyEnum.METHOD_KEY.value;
    private static final String NONCE_KEY = SignKeyEnum.NONCE_KEY.value;
    private static final String TIMESTAMP_KEY = SignKeyEnum.TIMESTAMP_KEY.value;
    private static final String SIGN_KEY = SignKeyEnum.SIGN_KEY.value;
    
    /**
     * 对请求内容签名
     * @param appid
     * @param secret
     * @param requestParams 请求参数（form + path 中的参数）
     * @param method 请求方法
     * @param path
     * @return
     */
    public Map<String, String> sign(String appid,
                                    String secret,
                                    String method,
                                    String path,
                                    Map<String, String> requestParams) {

        log.info("计算签名：appid = " + appid + ", secret = " + secret + ", method = " + method + ", path = " + path + ", requestParams = " + requestParams);

        // 请求签名后的返回内容
        Map<String,String> signResult = Maps.newHashMap();
        signResult.put(SignKeyEnum.APPID_KEY.value,appid);
        signResult.put(TIMESTAMP_KEY, String.valueOf(System.currentTimeMillis()/1000L));
        signResult.put(NONCE_KEY, RandomUtil.randomString(16));

        // 构建签名体
        Map<String,String> signBody = requestParams!=null?Maps.newHashMap(requestParams):Maps.newHashMap();
        signBody.putAll(signResult);
        signBody.put(METHOD_KEY, method);
        signBody.put(PATH_KEY, path);
        signResult.put(SIGN_KEY, genSign(secret,signBody));

        return signResult;
    }

    /**
     * 校验签名
     * @param requestParams 请求参数
     * @return
     */
    public Boolean verify(String secret,
                          String sign,
                          String nonce,
                          String timestamp,
                          Map<String, String> params) {

        log.info("验证签名：secret = " + secret + ", sign = " + sign + ", nonce = " + nonce + ", timestamp = " + timestamp + ", params = " + params);

        Assert.isTrue(StringUtils.hasText(sign),"验证签名："+SignKeyEnum.SIGN_KEY+"不合法");
        Assert.isTrue(StringUtils.hasText(nonce),"验证签名："+SignKeyEnum.NONCE_KEY+"不合法");

        // 验证时间戳是否合法（在宽容时间内）
        Assert.isTrue(SignUtils.verifyTimestamp(timestamp,signShaProperties.getTolerant()), "验证签名："+SignKeyEnum.TIMESTAMP_KEY+"不合法");

        // 验证签名值是否合法（在一定周期内是否已使用过）
        Assert.isTrue(cacheService.cacheSign(sign,signShaProperties.getTolerant()),"签名验证：sign 已经使用过");

        log.info("验证签名：构建签名体");

        Map<String, String> signBody = Maps.newHashMap(params);
        signBody.put(NONCE_KEY,nonce);
        signBody.put(TIMESTAMP_KEY,timestamp);

        // 验证签名
        String _sign = genSign(secret,signBody);

        log.info("验证签名：计算得到sign={}",_sign);

        return sign.equals(_sign);
    }


    /**
     * 生成 Sign 值 (排序、去空值、计算签名)
     * @param secret
     * @param signBody （包含业务参数、nonce、timestamp、accept、content-type）
     */
    private String genSign(String secret, Map<String,String> signBody) {

        log.info("计算签名：secret = " + secret + ", signBody = " + signBody);

        String signStr = buildSignBody(signBody);

        // 生成 Sign 值
        HmacAlgorithm hmacAlgorithm = null;

        Assert.hasLength(signShaProperties.getAlgorithm(),"计算签名：参数Algorithm未配置");

        try {
            hmacAlgorithm = HmacAlgorithm.valueOf(signShaProperties.getAlgorithm());
        } catch (IllegalArgumentException e) {
            log.error("计算签名：签名算法无效 = {}",signShaProperties.getAlgorithm());
        }

        Assert.notNull(hmacAlgorithm,"计算签名：签名算法无效");

        HMac mac = new HMac(hmacAlgorithm,secret.getBytes());

        return mac.digestHex(signStr);
    }

    private String buildSignBody(Map<String,String> signBody) {

        // 移除签名体中已有的 sign 参数（如果存在的话）
        signBody.remove(SIGN_KEY);

        // 获取 signBody 中的 key 集合
        List<String> keyList = Lists.newArrayList(signBody.keySet());

        // 排序
        Collections.sort(keyList);

        // 构建签名内容
        StringBuilder sb = new StringBuilder();

        for (String key:keyList) {
            String value = signBody.get(key);
            if(StringUtils.hasText(value)) {
                sb.append(key).append(value);
            }
        }

        log.info("构建签名体：签名原文 = {}",sb);

        return sb.toString();

    }

}