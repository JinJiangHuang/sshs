package com.teracloud.sshs.socket;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

public class SSHServerSocket extends SSHSocket{
	private static Logger logger = Logger.getLogger(SSHServerSocket.class);
	
	private ServerSocket server;
	
	public SSHServerSocket() {
		server();
	}
	
	public SSHServerSocket(int port) {
		this.port = port;
		server();
	}
	
	
	private void server(){
		try {
			server = new ServerSocket(port);
			port = server.getLocalPort();
			logger.info("服务端开启socket，端口号："+port);
		} catch (IOException e) {
			e.printStackTrace();
			close();
			logger.info("服务端socket异常已关闭");
		}
	}
	
	public void accept(){
		try {
			logger.info("正在等待客户端socket请求");
			socket = server.accept();//accept会一直阻塞，直到有socket连接请求。
			logger.info("socket连接成功，获取客户端socket输入输出流");
			socketReader = new BufferedReader(new InputStreamReader(
					socket.getInputStream(),"utf-8"));
			socketWriter = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream(), "utf-8"));
		} catch (IOException e) {
			close();
			logger.info("服务端socket连接异常已关闭,原因："+e.getMessage());
		}
	}
	/**
	 * 
	 */
	public void  close(){
		super.close();
		try {
			server.close();
		} catch (IOException e) {}
	}
	
	
	public ServerSocket getServer() {
		return server;
	}

	public void setServer(ServerSocket server) {
		this.server = server;
	}	
	
	
}
