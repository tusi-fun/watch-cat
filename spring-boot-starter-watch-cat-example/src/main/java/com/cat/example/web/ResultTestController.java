package com.cat.example.web;

import com.cat.example.ping.ResultPing;
import com.cat.result.ResultData;
import com.cat.watchcat.log.annotation.LogCat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 请求详情对象封装测试
 * @author hudongshan
 * @version 2021/11/22
 */
@Slf4j
@RestController
@RequestMapping(value = "result", produces = MediaType.APPLICATION_JSON_VALUE)
public class ResultTestController {

    @LogCat(actionGroup = "result", action = "case1", enableEvent = false)
    @PostMapping("result/{a}")
    public ResultData<ResultPing> strToLocalDate(@PathVariable String a) {

        log.info("a={}",a);

        return ResultData.ok().data(ResultPing.builder()
                .name("zhangsan")
                .phone("15188888888")
                .build());
    }


}