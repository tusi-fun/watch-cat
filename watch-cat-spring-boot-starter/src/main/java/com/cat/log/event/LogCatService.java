package com.cat.log.event;

import com.cat.common.RequestInfo;

/**
 * @author hudongshan
 * @version 2021/12/9
 */
public interface LogCatService {

	/**
	 * 日志通知
	 * @param requestInfo
	 * @return
	 */
	void callback(RequestInfo requestInfo);

}