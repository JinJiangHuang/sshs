package com.teracloud.sshs.exception;

public class ServerNoneFreePortException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -883110421784572690L;

	public ServerNoneFreePortException() {
		super("服务端没有空闲端口可使用。(如果设置端口限制，请尝试增加开放端口)");
	}
}
