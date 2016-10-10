package com.teracloud.sshs;

import com.teracloud.sshs.handler.impl.SystemOutHandler;


public class App {

	public static void main(String[] args) throws InterruptedException {
		Server server = new Server(new SystemOutHandler());
		server.start();
		
		Thread.sleep(10000);
		server.shutdown();
	}

}
