package com.teracloud.sshs;

import com.teracloud.sshs.exception.ServerConfigurationIllegalParamException;
import com.teracloud.sshs.exception.ServerNoneFreePortException;
import com.teracloud.sshs.handler.impl.SystemOutHandler;


public class App {

	public static void main(String[] args) throws InterruptedException, ServerConfigurationIllegalParamException, ServerNoneFreePortException {
		Server server = new Server(new SystemOutHandler());
		server.start();
		
		Thread.sleep(10000);
		server.shutdown();
	}

}
