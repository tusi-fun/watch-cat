package com.cat.example.bean.logcat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author hudongshan
 * @version 2022/7/6
 */
@Data
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Def extends Abc implements Serializable {

	private static final long serialVersionUID = -1123503700108449312L;

	String ddd;
	String eee;
	String fff;
}
