package com.cat.example.web;

import com.cat.example.bean.sign.SignPing;
import com.cat.result.ResultData;
import com.cat.watchcat.log.annotation.LogCat;
import com.cat.watchcat.sign.annotation.SignCat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 请求验签响应加签工具测试
 * @author hudongshan
 * @version 2022/10/18
 */
@Slf4j
@RestController
@RequestMapping(value = "example/signCatExample", produces = MediaType.APPLICATION_JSON_VALUE)
public class SignCatTestController {

    /**
     * json 提交
     * @param signPing
     * @return
     */
    @LogCat(actionGroup = "sign-cat", action = "case1", enableEvent = false)
    @SignCat
    @PostMapping("case1")
    public ResultData signCatCase1(SignPing signPing) {
        return new ResultData(200, "操作成功").data("");
    }

    /**
     * 表单提交
     * @param signPing
     * @return
     */
    @LogCat(actionGroup = "sign-cat", action = "case2", enableEvent = false)
    @SignCat
    @PostMapping("case2")
    public ResultData signCatCase2(@Valid SignPing signPing) {
        return new ResultData(200, "操作成功").data("");
    }
}