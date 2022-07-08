package com.cat.example.web;


import com.cat.common.AreaDetail;
import com.cat.example.bean.convert.JsonStrToLocalDate;
import com.cat.watchcat.log.annotation.LogCat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

    @LogCat(actionGroup = "convert", action = "convertExample", enableEvent = false)
    @PostMapping("strToAreaDetail")
    public String strToAreaDateil(@RequestParam AreaDetail areaCodes) {

        log.info("a={}",areaCodes);

        return "ok";
    }

    /**
     * 这里实际上没有用到 convert 的功能，纯靠在接收对象中增加注解（@JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "GMT+8", pattern="yyyy-MM-dd")）实现
     * @param a
     * @return
     */
    @LogCat(actionGroup = "convert", action = "convertExample", enableEvent = false)
    @PostMapping("jsonStrToLocalDateTime")
    public String jsonStrToLocalDateTime(@RequestBody JsonStrToLocalDate a) {

        log.info("a={}",a);

        return "ok";
    }

}