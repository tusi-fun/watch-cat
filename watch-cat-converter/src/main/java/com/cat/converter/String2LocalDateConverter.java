package com.cat.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * String类型时间戳格式参数 -> 接收对象中的 LocalDate
 * @author xy783
 * @version 20211211
 */
@Slf4j
public class String2LocalDateConverter implements Converter<String, LocalDate> {

    @Override
    public LocalDate convert(String timestampStr) {
        // 秒级时间戳 转 LocalDate
        return StringUtils.hasText(timestampStr)?Instant.ofEpochSecond(Long.parseLong(timestampStr)).atZone(ZoneOffset.of("+8")).toLocalDate():null;

    }
}
