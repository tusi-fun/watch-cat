package com.cat.example.web;

import com.cat.example.exception.LimitCatCase3Exception;
import com.cat.example.exception.LimitCatCase4Exception;
import com.cat.example.result.Result;
import com.cat.watchcat.limit.annotation.LimitCat;
import com.cat.watchcat.log.annotation.LogCat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 业务频率控制工具测试
 * @author hudongshan
 * @version 2021/11/22
 */
@Slf4j
@RestController
@RequestMapping(value = "limit-cat", produces = MediaType.APPLICATION_JSON_VALUE)
public class LimitCatTestController {

    /**
     * 非异常触发
     * @param a
     * @return
     */
    @LogCat(actionGroup = "limit-cat", action = "case1", enableEvent = false)
    @LimitCat(scene ="case1", key = "#a")
    @PostMapping("case1")
    public Result case1(String a) {
        return Result.ok();
    }

    /**
     * 异常触发
     * @param a
     * @return
     */
    @LogCat(actionGroup = "limit-cat", action = "case2", enableEvent = false)
    @LimitCat(scene ="case2", key = "#a", triggerFor = {IllegalArgumentException.class})
    @PostMapping("case2")
    public Result case2(Integer a) {

        if(a==1) {
            throw new IllegalArgumentException("异常触发测试");
        }

        return Result.ok();
    }

    /**
     * 异常+code 触发
     * @param a
     * @return
     */
    @LogCat(actionGroup = "limit-cat", action = "case3", enableEvent = false)
    @LimitCat(scene ="case3", key = "#a", triggerFor = {LimitCatCase3Exception.class},triggerForCode = {"2000"})
    @PostMapping("case3")
    public Result case3(Integer a) {

        if(a==1) {
            throw new LimitCatCase3Exception(2000,"异常触发测试");
        }

        if(a==2) {
            throw new LimitCatCase3Exception(2001,"异常触发测试");
        }

        return Result.ok();
    }

    /**
     * 异常+code(指定code来源字段) 触发
     * @param a
     * @return
     */
    @LogCat(actionGroup = "limit-cat", action = "case4", enableEvent = false)
    @LimitCat(scene ="case4", key = "#a", triggerFor = {LimitCatCase4Exception.class},triggerForCode = {"2000"}, triggerForCodeField = "getStatus")
    @PostMapping("case4")
    public Result case4(Integer a) {

        if(a==1) {
            throw new LimitCatCase4Exception(2000,"异常触发测试");
        }

        if(a==2) {
            throw new LimitCatCase4Exception(2001,"异常触发测试");
        }

        return Result.ok();
    }

    /**
     * 自定义异常提示
     * @param a
     * @return
     */
    @LogCat(actionGroup = "limit-cat", action = "case5", enableEvent = false)
    @LimitCat(scene ="case4", key = "#a", msg = "不要乱搞")
    @PostMapping("case5")
    public Result case5(Integer a) {

        return Result.ok();
    }

    /**
     * 异常触发（多个 LimitCat 同时使用）
     * @param a
     * @return
     */
    @LogCat(actionGroup = "limit-cat", action = "case6", enableEvent = false)
    @LimitCat(scene ="case5", key = "#a", triggerFor = {IllegalArgumentException.class})
    @LimitCat(scene ="case6", key = "#a", triggerFor = {IllegalArgumentException.class})
    @PostMapping("case6")
    public Result case6(Integer a) {

        if(a==1) {
            throw new IllegalArgumentException("异常触发测试");
        }

        return Result.ok();
    }

}