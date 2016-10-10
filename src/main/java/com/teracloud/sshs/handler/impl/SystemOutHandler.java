package com.teracloud.sshs.handler.impl;

import com.teracloud.sshs.handler.MessageHandler;

public class SystemOutHandler implements MessageHandler{

	public void execute(String message) {
		System.out.print(message);
	}

}
