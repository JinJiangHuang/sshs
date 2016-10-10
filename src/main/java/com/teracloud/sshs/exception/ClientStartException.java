package com.teracloud.sshs.exception;


public class ClientStartException extends Exception {

	private static final long serialVersionUID = -4592369324141429808L;
	
	public ClientStartException() {
		super("sshs 客户端启动异常");
	}
	
}
