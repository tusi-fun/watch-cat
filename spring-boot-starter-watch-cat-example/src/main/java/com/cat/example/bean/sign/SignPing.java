package com.cat.example.bean.sign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author hudongshan
 * @version 2022/12/14
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignPing {

	@NotBlank
	String name;

	@NotBlank
	String phone;

	Integer age;

	@NotBlank
	String address;
}