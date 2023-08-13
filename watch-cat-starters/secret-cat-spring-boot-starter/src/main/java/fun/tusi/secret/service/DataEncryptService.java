package fun.tusi.secret.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import fun.tusi.secret.config.SecretCatProperties;
import fun.tusi.secret.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 敏感数据加密处理入口
 * @author xy783
 */
@Slf4j
public class DataEncryptService {

    private final RedisTemplate redisTemplate;
    private final SecretCatProperties encryptCatProperties;

    public DataEncryptService(RedisTemplate redisTemplate,SecretCatProperties encryptCatProperties) {
        this.redisTemplate = redisTemplate;
        this.encryptCatProperties = encryptCatProperties;
    }

//    /**
//     * api安全相关缓存，对加密参数做hash缓存，防止重放攻击
//     * ========================================
//     */
//    public static final String SAFETY_API_ENCRYPT_HASH_KEY = "watch-cat:encrypt:data_hash:%s";
//    public static final Long SAFETY_API_ENCRYPT_HASH_TOLERANT = 30L;
//
//    /**
//     * 验证安全hash值是否存在
//     */
//    public Boolean cacheEncryptHash(String hash) {
//        return redisTemplate.opsForValue().setIfAbsent(String.format(SAFETY_API_ENCRYPT_HASH_KEY,hash),hash,SAFETY_API_ENCRYPT_HASH_TOLERANT, TimeUnit.MINUTES);
//    }

    /**
     * 解密客户端的 aes key
     */
    public byte[] decryptAesKey(String encryptKey) {
        try {
            RSA rsa = new RSA(encryptCatProperties.getPrivateKey(), null);
            return rsa.decrypt(encryptKey, KeyType.PrivateKey);
        } catch (CryptoException e) {
            e.printStackTrace();
            throw new SecretCatException("数据 encryptKey 解密失败");
        }
    }

    /**
     * 解密客户端提交的 encryptData（json 格式的原文）
     */
    public <T> T decryptDataToBean(byte[] aesKey,String encryptData,Class<T> beanClass) {

        // 解密原文
        String planTxt = decryptData(aesKey,encryptData);

        return JsonUtils.toBean(planTxt,beanClass);
    }

    /**
     * 解密客户端提交的 encryptData（json 格式的原文）
     */
    public String decryptData(byte[] aesKey,String encryptData) {
        try {

            // 初始化 AES
            SymmetricCrypto symmetricCrypto = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey);

            return symmetricCrypto.decryptStr(encryptData);

        } catch (CryptoException e) {
            throw new SecretCatException("数据 encryptData 解密失败");
        }
    }

    /**
     * 使用 AESKey 加密原文
     * @return
     */
    public String encryptData(byte[] aesKey,Object data) {
        return encryptData(aesKey, JsonUtils.toJson(data));
    }

    public String encryptData(byte[] aesKey,String data) {
        try {
            // 初始化 AES
            SymmetricCrypto symmetricCrypto = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey);

            return symmetricCrypto.encryptBase64(data);

        } catch (CryptoException e) {
            throw new SecretCatException("数据（aes加密的原文）加密失败");
        }
    }

    /**
     * 使用 RSA 加密 AESkey
     * @return
     */
    public static String encryptKey(byte[] aesKey,String privateKey,String publicKey) {

        try {
            RSA rsa = new RSA(privateKey,publicKey);

            // 加密aesKey
            byte[] encryptAesKeyByte = rsa.encrypt(aesKey,KeyType.PublicKey);
            return Base64.encode(encryptAesKeyByte);

        } catch (CryptoException e) {
            throw new SecretCatException("数据（aesKey）加密失败");
        }
    }
//
//    /**
//     * 20210422 对接 yapi 测试通过
//     */
//    public static void main(String[] args) throws Exception {
//
//        String encryptKey = "15c52f66fc2de3ef8e7fd111fe350c22c23149b06f7590444038354261a224b661cf28dc7bf555d5bfc98a876539a1e7a5431754e0e1f26124de1a68a98db3145a80c655104b211ad1fbb62bb308cb16097161a50eff7f57d354e4055e7b934b7d140e3f33c2fb812aeb4571688e54a80188ca2683066318f49c947ecf6b31eb";
//        String encryptData = "cTHAfhSTGL6/QpRYJxdSMtKi4Ib9PKiDnaTsOQX893E=";
//
//        RSA rsa = new RSA("MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIYX3tE4ah6GWVVnp2uYGnjDIkB+pg/TnSacSoPe+sAZSHyVOIDPn6WF8DBc25t7w7teP9vKhFohRibz+IKvunLfKRfZqGFhTYDZdo4VOwHZ2X/uIPwC/w6MqvxjbLVkX9F3pd5HbZxSK/ufblQWnOPeS1qQUbUxh42rvD73hpHTAgMBAAECgYA1YtOHIJq/RGXUpsv0/j2rzqSYYNPkgS+T662PCvtx957wWsiAIClDUSvrVUzpfkDTHBb4foxrBcxUPdW20he8iFnhQZMZvXhu3i+7QeVNV+ksEbAcL+1uKhHdwQ7Yde4IzIMQiPEzkcewKW8xmymon+zEZ4zmRajhTvJyhchgoQJBAMX1lsI4su+nT5RSzkRGIj/gywGiKngDsViP9IYnr9SshYym7Y0fVk+ssFAKUh8mS46Cxp5mKo/CNKtIHoM5x4cCQQCtaKCSbc3p1mwS4Syg9BiTCiHNfU8M6J+SsAJhZ3bvR04DIiItZYB+8+naF2jOf3dyiqUXn1kADCg4k9Kqvp5VAkBRwHePJA05jZX+wieu6GvSh4ou1YLGZ3gBBApOsOsbYFgS2wk1g6CIbN+vXmPFu3Hum7FczwJ6thA8QB9hwCXXAkB4vvvgOjlBdB743nY29QEPkeSLs4+Ry4EhoRFnRaYZYys9H01xEtZwj+LsC5TdBQDlbwkZ88kDMXPoQXZVkjmpAkEAnk/T2/OPuw0Ey8sYTfox/CCUmTK+YFO1AwFU7XX2WY8HqT56IcfAxoVFngTSJHTROLRRW0PD1guVZpimxwSK+g==", null);
//
//        byte[] aesKey = rsa.decrypt(encryptKey, KeyType.PrivateKey);
//
//        System.out.println("aesKey："+new String(aesKey));
//
//        // 初始化 AES
//        SymmetricCrypto symmetricCrypto = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey);
//
//        String data = symmetricCrypto.decryptStr(encryptData);
//
//        System.out.println(data);
//
//    }

//    public static byte[] decode(String key) {
//        return Validator.isHex(key) ? HexUtil.decodeHex(key) : Base64.decode(key);
//    }

    /**
     * 构建用户登录token
     */
//    public UserToken genUserToken(long uid, String phone){
//
//        // 生成Token
//        String tid = RandomUtil.simpleUUID(),
//                token = RandomUtil.simpleUUID();
//
//        // 缓存Token
//        appCache.cacheUserToken(tid,token,uid);
//
//        UserToken userToken = new UserToken();
//        userToken.setPhone(phone);
//        userToken.setTid(tid);
//        userToken.setToken(token);
//        return userToken;
//    }
//
//    public static void main(String[] args){
////         初始化 RSA
//        String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKcZnNZvUy+K0gbEzEhVSbdjaDPG3ecj2N2sxWIEBpzlbSXWAfU0HXT0kbMETjy0PwpSi7WqJ8pgeF+IExFEVdtXgxNxjTzNkUxMtcZMSKMehnkfNUWz93oHf0Hm9I+tGyvT+nm90nphSlqYQ9OiQ/Xbr+Ipmw0WexI0d3Uk4ILLAgMBAAECgYBpFgtzM7mt2XCx/wu1paVhZmu+vB8LYJje9+t986gw/XvB4q+ChE7OIYyLd3a4aGaI2FayXZr+g+LPuWS8ZCxVagDcHtdqCwGrpjsoURfKzAhfwDYVLSBUwgVLJ6mE/05zZbVMbUozrMq/6BSfL9QAgq2Z0O+LVkooIbjrfrmPQQJBAOfbKiLxBzqH4nRCtNQAojZEOp9tg60TxkRpWS38vAFZO1PIuAUiNqDAn38FqiP3S8r7cTJ8KIl+Pux+3kL4XuECQQC4gC08bSJii/Py550/gk9bgkpfH0Xt73YBvsNUodug9Aq5WMG/tBQhPuxnu2yg+3yAPBy/rqvEjRSzGpBjOfMrAkButUKYbphyBUJHGzb259qMqOWJDKTMNt37+oWfpMcsqavfZL8hjGWjOnauE0lbZRCmuoshfRFqHYL0L9v3BFthAkEAqjMMB+dBRhmHNHg3loO2g1tBElj+II27lcaN2L/rpKIcVnkrWpZbz4OFf/flMOhuJLnPZ4BR0mK3SrXhprepuwJANgE9/TxeaBFCKgj4zFZdDp+SnJletwaNWfCV2NXK7jZHAe4LK47x9TTp0nFrdULQpEa6rXxN6TPSQuHdWgkpFA==";
//        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnGZzWb1MvitIGxMxIVUm3Y2gzxt3nI9jdrMViBAac5W0l1gH1NB109JGzBE48tD8KUou1qifKYHhfiBMRRFXbV4MTcY08zZFMTLXGTEijHoZ5HzVFs/d6B39B5vSPrRsr0/p5vdJ6YUpamEPTokP126/iKZsNFnsSNHd1JOCCywIDAQAB";
//        RSA rsa = new RSA(privateKey,publicKey);
//
////        //////////
////        ///客户端//
////        //////////
//        String data = "{\"securityCode\":\"123456\"}";
//        System.out.println("客户端执行加密");
//        System.out.println("data:"+data);
//
//        // 随机生成密钥
//        byte[] aesKeyByte = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
//        System.out.println("aesKey:"+Base64.encode(aesKeyByte));
//
//        // 初始化 AES
//        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKeyByte);
//
//        // 加密原文，输出为Base64编码
//        String encryptBase64Data = aes.encryptBase64(data);
//
//        // 加密aesKey
//        byte[] encryptAesKeyByte = rsa.encrypt(aesKeyByte,KeyType.PublicKey);
//        String encryptBase64Key = Base64.encode(encryptAesKeyByte);
//
//        System.out.println("encryptBase64Data:"+encryptBase64Data);
//        System.out.println("encryptBase64Key:"+encryptBase64Key);
//
//
////        //////////
////        ///服务端//
////        //////////
//        System.out.println("服务端执行解密");
//        byte[] aesKey = rsa.decrypt(encryptBase64Key, KeyType.PrivateKey);
//        System.out.println("aesKey:"+Base64.encode(aesKey));
//        SymmetricCrypto symmetricCrypto = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey);
//        String data2 = symmetricCrypto.decryptStr(encryptBase64Data);
//        System.out.println("data:"+data2);
//    }

//    public static void main(String[] args) {
//        KeyPair keyPair = SecureUtil.generateKeyPair("RSA");
//
//        System.out.println("PrivateKey:"+Base64.encode(keyPair.getPrivate().getEncoded()));
//        System.out.println("PublicKey:"+Base64.encode(keyPair.getPublic().getEncoded()));
//    }

}
