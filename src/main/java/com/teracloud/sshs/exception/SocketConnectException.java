package com.teracloud.sshs.exception;

public class SocketConnectException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6056377483421818147L;
	
	public SocketConnectException() {
		super("sshs socket连接异常");
	}

}
