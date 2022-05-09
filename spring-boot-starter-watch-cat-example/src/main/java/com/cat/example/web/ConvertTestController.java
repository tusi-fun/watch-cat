package com.cat.example.web;


import com.cat.watchcat.log.annotation.LogCat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 请求参数转换工具测试
 * @author hudongshan
 * @version 2021/11/22
 */
@Slf4j
@RestController
@RequestMapping(value = "convert", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConvertTestController {

    @LogCat(actionGroup = "convert", action = "convertExample", enableEvent = false)
    @PostMapping("strToLocalDate/{a}")
    public String strToLocalDate(@PathVariable LocalDate a) {

        log.info("a={}",a);

        return "ok";
    }

    @LogCat(actionGroup = "convert", action = "convertExample", enableEvent = false)
    @PostMapping("strToLocalDateTime/{a}")
    public String strToLocalDateTime(@PathVariable LocalDateTime a) {

        log.info("a={}",a);

        return "ok";
    }

    @LogCat(actionGroup = "convert", action = "convertExample", enableEvent = false)
    @PostMapping("strToLocalTime/{a}")
    public String strToLocalTime(@PathVariable LocalTime a) {

        log.info("a={}",a);

        return "ok";
    }


}