package com.cat.example.web;

import com.cat.example.bean.Aa;
import com.cat.result.ResultData;
import com.cat.watchcat.limit.annotation.LimitCat;
import com.cat.watchcat.log.annotation.LogCat;
import com.cat.watchcat.secret.annotation.SecretCat;
import com.cat.watchcat.secret.annotation.VictoriasSecret;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 请求响应加解密工具测试
 * @author hudongshan
 * @version 2021/11/22
 */
@Slf4j
@RestController
@RequestMapping(value = "example/secretCatExample", produces = MediaType.APPLICATION_JSON_VALUE)
public class SecretCatTestController {

    @LogCat(actionGroup = "secret-cat", action = "case1", enableEvent = false)
    @SecretCat(encryptedPong = true, plainTextValid = true, preventReplay = true)
    @PostMapping("case1")
    public ResultData secretCatCase1(String a, String b, Aa aa) {
        aa.setPhone("18582461287");
        aa.setAddress("成都市武侯区广安大厦185号7楼708");
        aa.setEmail("524712128@qq.com");
        aa.setName("流畅度");
        aa.setIdCard("511234520119652147");
        return new ResultData(200, "操作成功").data(aa);
    }

    @LogCat(actionGroup = "secret-cat", action = "case2", enableEvent = false)
    @LimitCat(scene ="case1", key = "#a")
    @SecretCat(encryptedPong = true, plainTextValid = true, preventReplay = false)
    @PostMapping("case2")
    public ResultData secretCatCase2(String a, String b, Aa aa) {
        aa.setPhone("18582461287");
        aa.setAddress("成都市武侯区广安大厦185号7楼708");
        aa.setEmail("524712128@qq.com");
        aa.setName("流畅度");
        aa.setIdCard("511234520119652147");
        return new ResultData(200, "操作成功").data(aa);
    }

//    @LogCat(actionGroup = "secret-cat", action = "case3", enableEvent = false)
//    @SecretCat(encryptedPong = true, plainTextValid = true, preventReplay = false)
//    @SecretCat(encryptedPong = true, plainTextValid = true, preventReplay = false)
//    @PostMapping("case3")
//    public ResultData secretCatCase3(String a, String b, Aa aa) {
//        aa.setPhone("18582461287");
//        aa.setAddress("成都市武侯区广安大厦185号7楼708");
//        aa.setEmail("524712128@qq.com");
//        aa.setName("流畅度");
//        aa.setIdCard("511234520119652147");
//        return new ResultData(200, "操作成功").data(aa);
//    }

    @LogCat(actionGroup = "secret-cat", action = "case4", enableEvent = false)
    @PostMapping("case4")
    public ResultData secretCatCase4(String a, String b,
                                     @VictoriasSecret(plainTextValid = true, preventReplay = true) Aa aa) {
        aa.setPhone("18582461287");
        aa.setAddress("成都市武侯区广安大厦185号7楼708");
        aa.setEmail("524712128@qq.com");
        aa.setName("流畅度");
        aa.setIdCard("511234520119652147");
        return new ResultData(200, "操作成功").data(aa);
    }

}