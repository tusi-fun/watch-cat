package com.cat.watchcat.log.event;

import com.cat.watchcat.log.aspect.RequestInfo;
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
