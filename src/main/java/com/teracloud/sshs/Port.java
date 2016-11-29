package com.teracloud.sshs;

public class Port {
	
	public Port(int port) {
		this.port =port;
		this.status = Status.free;
	}
	
	public enum Status  {
        used, free;  
    } 
	
	private int port;
	private Status status;
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "端口："+port+"\t状态："+status;
	}
	
}
