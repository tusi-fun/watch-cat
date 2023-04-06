package com.cat.web;

import com.cat.watchcat.converter.area.service.AreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @AreaCat 数据接口实现
 * @author hudongshan
 * @version 2021/12/9
 */
@Slf4j
@Component
public class AreaServiceImpl implements AreaService {

	/**
	 * 解析 省、市、县、社区名称
	 * @param areaCodes 格式：省|市|县|社区
	 * @return
	 */
	@Override
	public Map<String, String> parseAreaCodes(String[] areaCodes) {

		Map<String, String> areas = new HashMap<>();
		areas.put("340000","四川省");
		areas.put("340800","成都市");
		areas.put("340803","锦江区");
		areas.put("340803401","xxx街道");

		return areas;
	}

}