package com.cat.bean.validator;

import com.cat.validator.DomainChecker;
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