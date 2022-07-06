package com.cat.example.bean.logcat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author hudongshan
 * @version 2022/7/6
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Abc implements Serializable {

	private static final long serialVersionUID = 2051006243483137294L;

	String aaa;
	String bbb;
	String ccc;
}
