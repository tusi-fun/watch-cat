package com.cat.web;

import com.cat.watchcat.sign.service.AppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @AreaCat 数据接口实现
 * @author hudongshan
 * @version 2021/12/9
 */
@Slf4j
@Component
public class AppServiceImpl implements AppService {

	@Override
	public String getAppSecret(String appId) {
		return "123456789";
	}
}