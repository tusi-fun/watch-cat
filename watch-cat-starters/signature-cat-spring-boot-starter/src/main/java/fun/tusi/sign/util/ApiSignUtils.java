package fun.tusi.sign.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import fun.tusi.sign.service.SignatureCatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 签名工具（适用于散列算法）
 * @author xy783
 */
@Slf4j
public class ApiSignUtils {

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
     * 校验签名（非对称算法）
     * @param params 请求参数
     * @return
     */
    public Boolean verify(String publicKey, SignAlgorithm algorithm, String sign, String nonce, String timestamp, Map<String, String> params) {

        log.info("验证签名: sign = " + sign + ", nonce = " + nonce + ", timestamp = " + timestamp + ", params = " + params);

        // 验证公钥提供者
//        SymmetricSignProvider symmetricSignProvider = platformSymmetricProperties.getSymmetric().get(pulicKeyProvider);

//        Assert.notNull(symmetricSignProvider,"验证签名: 签名提供者（"+pulicKeyProvider+"）不存在");
//        Assert.hasLength(symmetricSignProvider.getAlgorithm(),"验证签名: 签名提供者（"+pulicKeyProvider+"）参数Algorithm未配置");
//        Assert.hasLength(symmetricSignProvider.getPublicKey(),"验证签名: 签名提供者（"+pulicKeyProvider+"）参数PublicKey未配置");
        Assert.isTrue(StringUtils.hasText(sign),"验证签名: "+SIGN_KEY+"不合法");
        Assert.isTrue(StringUtils.hasText(nonce),"验证签名: "+NONCE_KEY+"不合法");

//        Assert.isTrue(SignUtils.verifyTimestamp(timestamp,symmetricSignProvider.getTolerant()), "验证签名: "+ApiSignKeyEnum.TIMESTAMP_KEY+"不合法");
//        // 验证签名值是否被使用过，存在则抛异常
//        Assert.isTrue(cacheService.cacheSign(sign,symmetricSignProvider.getTolerant()),"验证签名: sign 已经使用过");

        Map<String, String> signBody = new HashMap(params);
        signBody.put(NONCE_KEY,nonce);
        signBody.put(TIMESTAMP_KEY,timestamp);

        // 构建签名体
        String signStr = buildSignPlaintext(signBody);

//        SignAlgorithm signAlgorithm = null;
//
//        try {
//
//            signAlgorithm = SignAlgorithm.valueOf(symmetricSignProvider.getAlgorithm());
//
//        } catch (IllegalArgumentException e) {
//            log.error("验证签名: 签名算法无效 = {}",symmetricSignProvider.getAlgorithm());
//        }
//        Assert.notNull(signAlgorithm,"验证签名: 签名算法无效");

        Sign rsaSign = SecureUtil.sign(algorithm,null, publicKey);

        try {

            return rsaSign.verify(signStr.getBytes(), HexUtil.decodeHex(sign));

        } catch (Exception e) {

            log.error("验证签名: 执行值验证异常 = {}",e.getMessage());

            throw new IllegalArgumentException("验证签名: sign格式有误，必须为16进制字符");
        }

    }

    /**
     * 校验签名（摘要算法）
     * @param secret    签名密钥
     * @param algorithm 签名算法
     * @param params    业务参数
     * @param sign      签名值
     * @param nonce     随机数
     * @param timestamp unix 时间戳
     * @return
     */
    public static boolean verify(String secret, HmacAlgorithm algorithm, String sign, String nonce, String timestamp, Map<String, String> params) {

        log.info("ApiSignUtils4Sha > verify > 入参：secret=" + secret + ", algorithm=" + algorithm + ", sign=" + sign + ", nonce=" + nonce + ", timestamp=" + timestamp + ", params=" + params);

        //判断入参是否为空
//        if(secret==null) {
//            throw new SignatureCatException("配置策略：策略对象为空");
//        }
//
//        if(algorithm==null) {
//            throw new SignatureCatException("配置策略：策略对象为空");
//        }
//
//        if(!StringUtils.hasText(sign)) {
//            throw new SignatureCatException("随朷数："+ NONCE_KEY +"不合法");
//        }
//
//        if(!StringUtils.hasText(timestamp)) {
//            throw new SignatureCatException("时间户："+ TIMESTAMP_KEY +"不合法");
//        }
//
//        if(params==null) {
//            throw new SignatureCatException("业务参数：params 不合法");
//        }

        // 判断策略对象是否合法
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

    /**
     * 构建签名原文
     * @param signBody
     * @return
     */
    public static String buildSignPlaintext(Map<String,String> signBody) {

        // 移除参与签名参数中已有的 sign 参数（如果存在）
        signBody.remove(SIGN_KEY);

        // 获取 signBody 中的 key 集合
        List<String> keyList = new ArrayList(signBody.keySet());

        // 排序
        Collections.sort(keyList);

        // 构建签名内容
        StringBuilder sb = new StringBuilder();

        for (String key : keyList) {

            String value = signBody.get(key);

            // 移除空値
            if(StringUtils.hasText(value)) {

                sb.append(key).append(value);
            }
        }

        log.info("ApiSignUtils4Sha > 签名原文 = {}",sb);

        return sb.toString();
    }

}