package com.cat.log.event;

import com.cat.common.RequestInfo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author hudongshan
 * @version 2021/12/8
 */
@Getter
public class LogCatEvent extends ApplicationEvent {

	private final RequestInfo requestInfo;

	public LogCatEvent(Object source, RequestInfo requestInfo) {
		super(source);
		this.requestInfo = requestInfo;
	}
}
