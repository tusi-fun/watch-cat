package com.cat.converter.area.service;

import java.util.Map;

/**
 * @author hudongshan
 * @version 2021/12/9
 */
public interface AreaService {

	/**
	 * 查询地区列表（解析专用）
	 * @param areaCodes  省\市\县\街道 code
	 * @return
	 */
	Map<String,String> parseAreaCodes(String[] areaCodes);

}