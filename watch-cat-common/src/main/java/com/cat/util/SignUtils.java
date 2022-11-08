package com.cat.util;

/**
 * 验证签名时间戳是否合法
 * @author hudongshan
 * @version 20210801
 */
public class SignUtils {

    public static boolean verifyTimestamp(String timestamp,Long tolerant) {

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
