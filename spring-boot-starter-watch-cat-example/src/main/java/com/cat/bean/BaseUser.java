package com.cat.bean;

import cn.hutool.core.util.DesensitizedUtil;
import com.cat.watchcat.sensitive.annotation.SensitiveField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hudongshan
 * @version 2021/11/29
 */
@Data
public class BaseUser implements Serializable {

	private static final long serialVersionUID = -1284296471023223393L;

	@SensitiveField(type = DesensitizedUtil.DesensitizedType.CHINESE_NAME)
	private String realName;

}
