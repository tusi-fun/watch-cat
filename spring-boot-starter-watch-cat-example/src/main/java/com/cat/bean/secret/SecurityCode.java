package com.cat.bean.secret;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hudongshan
 * @version 2022/10/18
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityCode implements Serializable{

	private static final long serialVersionUID = 4926755930730296132L;

	String securityCode;

//	@NotBlank
	String password;
}
