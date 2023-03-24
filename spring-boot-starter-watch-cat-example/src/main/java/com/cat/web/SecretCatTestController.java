package com.cat.web;

import com.cat.bean.Aa;
import com.cat.bean.SecretCatObj;
import com.cat.bean.secret.SecurityCode;
import com.cat.result.Result;
import com.cat.watchcat.limit.annotation.LimitCat;
import com.cat.watchcat.log.annotation.LogCat;
import com.cat.watchcat.secret.annotation.SecretCat;
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

//    @LogCat(actionGroup = "secret-cat", action = "case1", enableEvent = false)
//    @SecretCat(encryptPong = true, verifyPlaintext = true, plaintextParameter = "#testPing.securityCode")
//    @PostMapping("case0")
//    public ResultData secretCatCase0(String a, String b, TestPing testPing) {
//        Aa aa = new Aa();
//        aa.setPhone("18582461287");
//        aa.setAddress("成都市武侯区广安大厦185号7楼708");
//        aa.setEmail("524712128@qq.com");
//        aa.setName("流畅度");
//        aa.setIdCard("511234520119652147");
//        return new ResultData(200, "操作成功").data(aa);
//    }

    @LogCat(actionGroup = "secret-cat", action = "case1", enableEvent = false)
    @SecretCat(encryptPong = true, verifyPlaintext = true, plaintextParameter = "securityCode")
    @PostMapping("case1")
    public Result secretCatCase1(String a, String b, SecurityCode securityCode) {
        Aa aa = new Aa();
        aa.setPhone("18582461287");
        aa.setAddress("成都市武侯区广安大厦185号7楼708");
        aa.setEmail("524712128@qq.com");
        aa.setName("流畅度");
        aa.setIdCard("511234520119652147");
        return Result.ok().data(aa);
    }

    @LogCat(actionGroup = "secret-cat", action = "case2", enableEvent = false)
    @LimitCat(scene ="case1", key = "#a")
    @SecretCat(encryptPong = true, verifyPlaintext = true, plaintextParameter = "aa")
    @PostMapping("case2")
    public Result secretCatCase2(String a, String b, Aa aa) {
        aa.setPhone("18582461287");
        aa.setAddress("成都市武侯区广安大厦185号7楼708");
        aa.setEmail("524712128@qq.com");
        aa.setName("流畅度");
        aa.setIdCard("511234520119652147");
        return Result.ok().data(aa);
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

    @SecretCat(encryptPong = true, verifyPlaintext = true, plaintextParameter = "")
    @LogCat(actionGroup = "secret-cat", action = "case4", enableEvent = false)
    @PostMapping("case4")
    public Result secretatCase4(String a, String b, SecretCatObj secretCatObj) {
        secretCatObj.setPhone("18582461287");
        secretCatObj.setAddress("成都市武侯区广安大厦185号7楼708");
        secretCatObj.setEmail("524712128@qq.com");
        secretCatObj.setName("流畅度");
        secretCatObj.setIdCard("511234520119652147");
        return Result.ok().data(secretCatObj);
    }

}