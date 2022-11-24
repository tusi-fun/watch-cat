package com.cat.example.bean.validator;

import cn.hutool.core.util.DesensitizedUtil.DesensitizedType;
import com.cat.validator.DomainChecker;
import com.cat.watchcat.sensitive.annotation.SensitiveField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;


/**
 * @author hudongshan
 * @version 2021/11/22
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidatorCheckerVO implements Serializable {

    private static final long serialVersionUID = 659863927348642929L;

    @DomainChecker
    String domain;

}