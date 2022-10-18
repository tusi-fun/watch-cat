package com.cat.example.bean.secret;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hudongshan
 * @version 2022/10/18
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestPing implements Serializable{

	private static final long serialVersionUID = -7987453990338822476L;

	SecurityCode securityCode;
}
