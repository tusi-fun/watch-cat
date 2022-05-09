package com.cat.watchcat.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;

/**
 * String类型时间戳格式参数 -> 接收对象中的 LocalTime
 * @author hudongshan
 * @version 20211211
 */
@Slf4j
@Component
public class String2LocalTimeConverter implements Converter<String, LocalTime>{

    @Override
    public LocalTime convert(String timestampStr) {

        log.info("String2LocalTimeConverter->{}",timestampStr);

        // 秒级时间戳 转 LocalTime
        return StringUtils.hasText(timestampStr)?Instant.ofEpochSecond(Long.parseLong(timestampStr)).atZone(ZoneOffset.of("+8")).toLocalTime():null;

    }
}
