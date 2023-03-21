package com.cat.util;

/**
 * 验证签名时间戳是否合法
 * @author hudongshan
 * @version 20210801
 */
public class SignUtils {

    /**
     * 验证时间戳是否在容差范围内
     * @param timestamp unix 时间戳
     * @param tolerant 秒
     * @return
     */
    public static boolean verifyTimestamp(String timestamp, Long tolerant) {

        if(!StringUtils.hasText(timestamp)) {
            return false;
        }

        try {

            Long t = Long.parseLong(timestamp);

            return Math.abs(System.currentTimeMillis() / 1000L - t) <= tolerant;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }
}
