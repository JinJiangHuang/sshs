package com.teracloud.sshs;

import com.teracloud.sshs.handler.impl.SystemOutHandler;


public class App {

	public static void main(String[] args) {
		Server server = new Server(new SystemOutHandler());
		server.start();
	}

}
