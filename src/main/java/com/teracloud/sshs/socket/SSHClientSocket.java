package com.teracloud.sshs.socket;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.teracloud.sshs.exception.SocketConnectException;

public class SSHClientSocket extends SSHSocket{
	private static Logger logger = Logger.getLogger(SSHClientSocket.class);
	
	private String server;
	
	public SSHClientSocket(String server,int port) {
		this.server = server;
		this.port = port;
	}
	
	public void connect() throws SocketConnectException{
		try {
			logger.info("服务端 地址:"+server+"\t端口："+port);
			socket = new Socket(server, port);
			socketWriter = new PrintWriter(socket.getOutputStream());
			socketReader = new BufferedReader(new InputStreamReader(
					socket.getInputStream(),"utf-8"));
			logger.info("与服务端建立socket连接成功");
		}catch (Exception e) {
			logger.info("与服务端建立socket连接失败。原因:"+e.getMessage());
			throw new SocketConnectException();
		}
	}

	public String getServer() {
		return server;
	}
	
}
