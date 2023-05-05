package com.cat.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * String类型时间戳格式参数 -> 接收对象中的 LocalDateTime
 * @author hudongshan
 * @version 20211211
 */
@Slf4j
public class String2LocalDateTimeConverter implements Converter<String, LocalDateTime> {

    @Override
    public LocalDateTime convert(String timestampStr) {

        log.info("String2LocalDateTimeConverter->{}",timestampStr);

        // 秒级时间戳 转 LocalDateTime
        return StringUtils.hasText(timestampStr)?Instant.ofEpochSecond(Long.parseLong(timestampStr)).atZone(ZoneOffset.of("+8")).toLocalDateTime():null;
    }
}
