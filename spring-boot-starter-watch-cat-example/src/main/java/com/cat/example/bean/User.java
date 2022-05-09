package com.cat.example.bean;

import cn.hutool.core.util.DesensitizedUtil;
import com.cat.watchcat.sensitive.annotation.SensitiveField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hudongshan
 * @version 2021/11/29
 */
@Data
public class User extends BaseUser implements Serializable {

	private static final long serialVersionUID = 2732414534938554508L;

	@SensitiveField(type = DesensitizedUtil.DesensitizedType.PASSWORD)
	private String password;

	@SensitiveField(type = DesensitizedUtil.DesensitizedType.ID_CARD)
	private String idCard;

	@SensitiveField(type = DesensitizedUtil.DesensitizedType.MOBILE_PHONE)
	private String phone;

}
