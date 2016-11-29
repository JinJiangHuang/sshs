package com.teracloud.sshs.conf;

import com.teracloud.sshs.Port;

/**
 * 服务端配置文件
 * @author 陈威
 *
 */
public class ServerConfigure {
	private Port[] openPorts;
	private boolean isLimitPort;
	
	

	public Port[] getOpenPorts() {
		return openPorts;
	}
	public void setOpenPorts(Port[] openPorts) {
		this.openPorts = openPorts;
	}
	public boolean isLimitPort() {
		return isLimitPort;
	}
	public void setLimitPort(boolean isLimitPort) {
		this.isLimitPort = isLimitPort;
	}
	
	
	
}
