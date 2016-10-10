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
			logger.info("开始建立socket连接...");
			socket = new Socket(server, port);
			socketWriter = new PrintWriter(socket.getOutputStream());
			socketReader = new BufferedReader(new InputStreamReader(
					socket.getInputStream(),"utf-8"));
			logger.info("socket连接建立完毕<<");
		}catch (Exception e) {
			e.printStackTrace();
			throw new SocketConnectException();
		}
	}

	public String getServer() {
		return server;
	}
	
}
