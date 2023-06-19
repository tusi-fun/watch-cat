package com.cat.log.event;

/**
 * 日志持久化接口
 * @author xy783
 */
public interface LogCatService {

	/**
	 * 日志通知
	 * @param requestInfo
	 * @return
	 */
	void callback(RequestInfo requestInfo);

}