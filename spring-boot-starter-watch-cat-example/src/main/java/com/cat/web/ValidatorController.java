package com.cat.web;

import com.cat.bean.validator.ValidatorCheckerVO;
import com.cat.result.Result;
import com.cat.watchcat.limit.annotation.LimitCat;
import com.cat.watchcat.log.annotation.LogCat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author hudongshan
 * @version 2021/11/22
 */
@Slf4j
@RestController
@RequestMapping(value = "validator", produces = MediaType.APPLICATION_JSON_VALUE)
public class ValidatorController {

    /**
     * 验证域名
     * @param validatorCheckerVO
     * @return
     */
    @LogCat(actionGroup = "validator", action = "case1", enableEvent = false)
    @LimitCat(scene ="case1", key = "#a")
    @PostMapping("case1")
    public Result case1(@Valid ValidatorCheckerVO validatorCheckerVO) {
        return Result.ok();
    }


}