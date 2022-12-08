package com.cat.watchcat.sign.service;

import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 用户登录会话缓存获取
 * @author hudongshan
 */
@Slf4j
public class CacheService {

    public static final String SAFETY_API_SIGN_KEY = "safety:api_sign:%s";

    private final RedisTemplate wcRedisTemplate;

    public CacheService(RedisTemplate wcRedisTemplate) {
        this.wcRedisTemplate = wcRedisTemplate;
    }

    /**
     * 验证 sign 值是否存在
     * @param sign
     */
    public Boolean cacheSign(String sign,Long timeout){
        return wcRedisTemplate.opsForValue().setIfAbsent(String.format(SAFETY_API_SIGN_KEY, SecureUtil.md5(sign)),sign,timeout,TimeUnit.SECONDS);
    }

}