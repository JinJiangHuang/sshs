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
	private boolean isConnect;
	
	public SSHServerSocket() {
		server();
	}
	
	private void server(){
		try {
			server = new ServerSocket(port);
			port = server.getLocalPort();
			logger.info("ssh socket服务端开启服务，端口号："+port);
	
		} catch (IOException e) {
			e.printStackTrace();
			close();
			logger.info("ssh socket服务端异常已关闭");
		}
	}
	
	public void accept(){
		try {
			logger.info("正在等待ssh socket客户端请求...");
			socket = server.accept();//accept会一直阻塞，直到有socket连接请求。
			
			logger.info("收到ssh socket客户端请求，创建读\\写流");
			socketReader = new BufferedReader(new InputStreamReader(
					socket.getInputStream(),"utf-8"));
			socketWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
			
			this.isConnect = true;
			logger.info("读\\写流创建完毕");
		} catch (IOException e) {
			e.printStackTrace();
			close();
			logger.info("ssh socket服务端异常已关闭");
		}
	}
	
	public void  close(){
		super.close();
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	 public ServerSocket getServer() {
		return server;
	}

	public void setServer(ServerSocket server) {
		this.server = server;
	}

	public boolean isConnect() {
		return isConnect;
	}

	public void setConnect(boolean isConnect) {
		this.isConnect = isConnect;
	}

	
	class CheckConnectRunnable implements Runnable{
		private SSHServerSocket server;
		public CheckConnectRunnable(SSHServerSocket server) {
			this.server = server;
		} 
		public void run() {
			while(server.isConnect){
				try{
				     server.getSocket().sendUrgentData(0xFF);
				}catch(Exception e){
					server.setConnect(false);
					try {
						server.getSocket().shutdownInput();
						server.getSocket().shutdownOutput();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		
	}
	
}
