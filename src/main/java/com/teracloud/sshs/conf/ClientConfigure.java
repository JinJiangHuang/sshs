package com.teracloud.sshs.conf;
/**
 * Client 类的配置信息
 * @author TAES
 *
 */
public class ClientConfigure {
	//terminal的配置
	private String sshHost;
	private int sshPort;
	private String sshUsr;
	private String sshPwd;

	
	//sokcet配置
	private String socketHost;
	private int socketPort;
	
	public ClientConfigure(String sshUsr,String sshPwd,String socketHost,int socketPort) {
		sshHost = "127.0.0.1";
		sshPort = 22;
		this.sshUsr = sshUsr;
		this.sshPwd = sshPwd;
		this.socketHost = socketHost;
		this.socketPort = socketPort;
	}
	
	
	
	public String getSshHost() {
		return sshHost;
	}
	public void setSshHost(String sshHost) {
		this.sshHost = sshHost;
	}
	public int getSshPort() {
		return sshPort;
	}
	public void setSshPort(int sshPort) {
		this.sshPort = sshPort;
	}
	public String getSshUsr() {
		return sshUsr;
	}
	public void setSshUsr(String sshUsr) {
		this.sshUsr = sshUsr;
	}
	public String getSshPwd() {
		return sshPwd;
	}
	public void setSshPwd(String sshPwd) {
		this.sshPwd = sshPwd;
	}
	public String getSocketHost() {
		return socketHost;
	}
	public void setSocketHost(String socketHost) {
		this.socketHost = socketHost;
	}
	public int getSocketPort() {
		return socketPort;
	}
	public void setSocketPort(int socketPort) {
		this.socketPort = socketPort;
	}



	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("");
		return sb.toString();
	}
	
}
