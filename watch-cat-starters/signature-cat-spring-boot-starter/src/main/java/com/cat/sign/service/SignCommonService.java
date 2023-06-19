package com.cat.sign.service;

import cn.hutool.crypto.SecureUtil;
import com.cat.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

/**
 * 用户登录会话缓存获取
 * @author xy783
 */
@Slf4j
public class SignCommonService {

    public static final String SAFETY_API_SIGN_KEY = "safety:api_sign:%s";

    private final RedisTemplate redisTemplate;

    public SignCommonService(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 验证 sign 值是否存在
     * @param sign
     */
    public Boolean checkSign(String sign, Duration timeout) {
        return redisTemplate.opsForValue().setIfAbsent(String.format(SAFETY_API_SIGN_KEY, SecureUtil.md5(sign)), sign, timeout);
    }

    /**
     * 验证 timestamp 是否在容差范围内
     * @param timestamp unix 时间戳
     * @param tolerant 宽容时间
     * @return
     */
    public boolean checkTimestampTolerant(String timestamp, Duration tolerant) {

        if(!StringUtils.hasText(timestamp)) {
            return false;
        }

        try {
            return Math.abs((System.currentTimeMillis() / 1000L) - Long.parseLong(timestamp)) <= tolerant.getSeconds();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}