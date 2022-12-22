package com.cat.bean.convert;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author hudongshan
 * @version 2022/7/7
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonStrToLocalDate implements Serializable {

	private static final long serialVersionUID = -8238760358997771755L;

	@JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime a;

	@JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "GMT+8", pattern="yyyy-MM-dd")
	private LocalDate b;
}
