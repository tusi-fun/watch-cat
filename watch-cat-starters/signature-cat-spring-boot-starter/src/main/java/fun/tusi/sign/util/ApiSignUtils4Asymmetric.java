package fun.tusi.sign.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 接口加签、验签工具（适用于非对称算法RSA、SM2等）
 * @author xy783
 */
@Slf4j
public class ApiSignUtils4Asymmetric {

    public static final String
            PATH_KEY = "path",
            CONTENT_MD5_KEY = "content-md5",
            METHOD_KEY = "method",
            TIMESTAMP_KEY = "timestamp",
            NONCE_KEY = "nonce",
            SIGN_KEY = "sign";

    /**
     * 对请求内容签名
     * @param requestParams 请求参数（form + path 中的参数）
     * @param method 请求方法
     * @param path
     * @return
     */
    public Map<String, String> sign(String publicKey, String privateKey, SignAlgorithm algorithm, String method, String path, Map<String, String> requestParams) {

        log.info("ApiSignUtils4Asymmetric > sign > 入参 publicKey=" + publicKey + ", privateKey=" + privateKey + ", algorithm=" + algorithm + ", method=" + method + ", path=" + path + ", requestParams=" + requestParams);

        // 请求签名后的返回内容
        Map<String,String> signResult = new HashMap();
        signResult.put(TIMESTAMP_KEY, String.valueOf(System.currentTimeMillis()/1000L));
        signResult.put(NONCE_KEY, RandomUtil.randomString(16));

        // 构建签名体
        Map<String,String> signBody = requestParams!=null?new HashMap(requestParams):new HashMap();
        signBody.putAll(signResult);
        signBody.put(METHOD_KEY,method);
        signBody.put(PATH_KEY,path);
        signResult.put(SIGN_KEY, genSign(publicKey,privateKey,algorithm,signBody));

        return signResult;
    }

    /**
     * 校验签名
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
     * 生成 Sign 值 (排序、去空值、计算签名)
     * @param signBody （包含业务参数、nonce、timestamp、accept、content-type）
     */
    private String genSign(String publicKey,String privateKey,SignAlgorithm signAlgorithm,Map<String,String> signBody) {

        log.info("计算签名: signBody = " + signBody);

        String signStr = buildSignPlaintext(signBody);

        // 生成 Sign 值
//        SymmetricSignProvider symmetricSignProvider = platformSymmetricProperties.getSymmetric().get(pulicKeyProvider);

//        Assert.notNull(symmetricSignProvider,"计算签名: 签名提供者（"+pulicKeyProvider+"）不存在");
//        Assert.hasLength(symmetricSignProvider.getAlgorithm(),"计算签名: 签名提供者（"+pulicKeyProvider+"）参数Algorithm未配置");
//        Assert.hasLength(symmetricSignProvider.getPublicKey(),"计算签名: 签名提供者（"+pulicKeyProvider+"）参数PublicKey未配置");
//        Assert.hasLength(symmetricSignProvider.getPrivateKey(),"计算签名: 签名提供者（"+pulicKeyProvider+"）参数PrivateKey未配置");

//        SignAlgorithm signAlgorithm = null;
//
//        try {
//
//            signAlgorithm = SignAlgorithm.valueOf(symmetricSignProvider.getAlgorithm());
//
//        } catch (IllegalArgumentException e) {
//
//            log.error("计算签名：签名算法无效 = {}",symmetricSignProvider.getAlgorithm());
//        }
//
//        Assert.notNull(signAlgorithm,"计算签名: 签名算法无效");

        Sign sign = SecureUtil.sign(signAlgorithm, privateKey, publicKey);

        return HexUtil.encodeHexStr(sign.sign(signStr.getBytes()));
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

        log.info("ApiSignUtils > buildSignPlaintext = {}", sb);

        return sb.toString();
    }

//    public static void main(String[] args) {
//
//        // 生成密钥对
//        KeyPair pair = SecureUtil.generateKeyPair("RSA");
//
//        String privateKey = Base64.encode(pair.getPrivate().getEncoded());
//        System.out.println("privateKey = " + privateKey);
//
//        String publicKey = Base64.encode(pair.getPublic().getEncoded());
//        System.out.println("publicKey = " + publicKey);
//
//        byte[] data = "我是一段测试字符串".getBytes();
//        Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA,privateKey,publicKey);
//
//        PrivateKey privateKey = SecureUtil.generatePrivateKey("RSA",Base64.decode("MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCPPCZWczCLCgAcPdGQTI5lgxS/47KSd8OZuNzcZnBR04TkWgIyfTNLesWXswznEKlV/b4fba3oko07ALE+UT7CvDjQTxBnmOhGg96EX+F7HGfSAnxlvqCN8zsQ+AmYbsMoKhFznhj0eIQPKOLaSCcoQGh9UhOOOP91jkfmgc5O8BuSBYScaxf/P30rhjoUD3eJg1Rdz+sRYt7w3BMcE4LQBZ3ZmlWBogKFbS2jvYz7bIjpui/Qai/YWd1/MxlFQV0RY10d0yrK2z87VHMnPZj8WxClbv/Jfawc1+4PtbEhmNQnVuNMxFg/kRM++q6zUN+x1Us0cCtv7JKBNCKqmornAgMBAAECggEAEn30gSK7+Kdg3kSI4rVPiQGEU0XFTy2RHi6ErRu4kofZV1XbhMMvNSZzjiM6njZDdmuc+RfC5QzOmPh86Yu5q5B7UUFz37wY+Mmbl8F9LdS7/pv9jXEv70Ogs48iRgC+x1evCxs1I5LPkdl4eC/qhkLSeqN3DNyE9ptY4wSNKxmY4AeJhzcEZITwZ6/5B8jAQDT3+NjZ7N+qYOtDL1BAURjziNel42j5XYcHtqTDwFfw4lNFxRl7p8JjRyMJN05VziokuprxJxT70rsc/Cj1GiG6ajfEMcd+hf7F/4kj2KKWOyWLH6RdAC/vJB8PDO2L3v3vJyXC3rocknzcoBAMWQKBgQDW1gnIA+x7vmGtkZlNTI7+rr67PrNJIkQFXX9NbCXFEHgRCD3ASrCaJ5Uyj/zpICYMiYPTQt9RI2whpPhiV4XiLGQYqZWrUtq6Npz0K5vHV2/CUsfDADCNqKf8d4pOF+aw5Iz03mNqVfMqiRpctw/D9PQvr3hmUFa6U4TjT4eivQKBgQCqrf4MsSrXkfGZ2Ml5Dj+CqK/WNwUR1LBQvPnUlhOSrshhOXf5Hqa2FIzv+5+mCB/upX6zria6e4Aky9kYtQuYh96IemwpJXVP/MqgcDZHjQ1MNOAbbVx65x9kxX1PUUUBtuuCBEEj/pYnR3bfCpusC33U5fkCjzt1lbFz6d0wcwKBgQCf3n0+43SajQzg/aUn+Z27dkwmyLzf4mjd2Tq/YYglwmCmxAw3yRzsSiJjkvSwgqTt1XNMxcmq9oIj80CG5fySV1hRZfkjma98vOFFbiIpUC6xW6qduMlo0SXY4RgBjxWzcxBVtBwk6Drg30/HyM4pk2IXDiu5b3VqiHYuNWOIvQKBgQCiuzOuvDihwid9TMGDJv+MvvKvplOyFXxp7lOwycotn3GzqQxnPD8s+VkK8njb34E6kxXDkPah9GBQNP9vXCaLLngQ3dBERs+cDOTQpUxLWgPktcE6xUFYeQZomRcJye2mXdYbZ50ICZL2Ll/69m1IUsiNdRq+wvuo96lyX/ahWwKBgQCW4FCwbZG9mcUfzfXTvx/FdMI++QMZOrtRhtJL6K6Sh8OmkW0VPnGKWrwOiblN4yKjdHQ7eG5t8ielTzJebk6TpxlXoLs4jz6YEoW23sscuA+YlErBoX5elDCqhl/DOSaP4EUbDJUQwmjNb/ajDbhMQxqbSSKF9WD/RIsKlfpavQ=="));
//
//        System.out.println(privateKey);
//
//        //签名
//        byte[] signed = sign.sign(data);
//
//        String signStr = HexUtil.encodeHexStr(signed);
//        System.out.println(signStr);
//
//        //验证签名
//        boolean verify = sign.verify(data, HexUtil.decodeHex(signStr));
//
//        System.out.println(verify);
//    }

}