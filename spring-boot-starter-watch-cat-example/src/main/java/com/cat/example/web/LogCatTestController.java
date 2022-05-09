package com.cat.example.web;

import com.cat.example.result.Result;
import com.cat.watchcat.log.annotation.LogCat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日志工具测试
 * @author hudongshan
 * @version 2021/11/22
 */
@Slf4j
@RestController
@RequestMapping(value = "log-cat", produces = MediaType.APPLICATION_JSON_VALUE)
public class LogCatTestController {

    @LogCat(actionGroup = "log-cat", action = "case1", enableEvent = false)
    @PostMapping("case1")
    public Result case1(String a) {
        return Result.ok();
    }
}