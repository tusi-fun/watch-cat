package com.cat.web;

import com.cat.bean.logcat.Gh;
import com.cat.result.Result;
import com.cat.watchcat.log.annotation.LogCat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public Result case1(String[] a) throws InterruptedException {

        return Result.ok();
    }

    @LogCat(actionGroup = "log-cat", action = "case2", enableEvent = false)
    @PostMapping("case2")
    public Result case2(Gh gh) throws InterruptedException {

        return Result.ok();
    }

    @LogCat(actionGroup = "log-cat", action = "case2", enableEvent = false)
    @PostMapping("case3")
    public Result case3(Gh gh) throws InterruptedException {

        Thread.sleep(1000);

        if(1==1) {
            throw new RuntimeException("异常日志");
        }

        return Result.ok();
    }

    @LogCat(actionGroup = "log-cat", action = "case4", enableEvent = false)
    @PostMapping("case4")
    public Result case4(@RequestBody Gh gh) {

        return Result.ok();
    }
}