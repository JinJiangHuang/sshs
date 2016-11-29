package com.teracloud.sshs.exception;
/**
 * 服务端配置文件参数异常
 * @author TAES
 *
 */
public class ServerConfigurationIllegalParamException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2769776090949266625L;

	public ServerConfigurationIllegalParamException() {
		super("服务端配置文件参数异常。(请检查 sshs 的配置文件是正确)");
	}
}
