package com.teracloud.sshs;


import java.io.BufferedReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.teracloud.sshs.handler.MessageHandler;
import com.teracloud.sshs.socket.SSHServerSocket;


public class Server {
	//Server status
	public static final int STARTED = 1;
	public static final int WAITTING = 0;
	public static final int SHUTDOWNED = -1;
	public static final int UNCONNECTED = -2;
	
	private static Logger logger = Logger.getLogger(Server.class);
	
	private SSHServerSocket socket;
	private MessageHandler handler;
	private int status = WAITTING;
	
	public Server(MessageHandler handler) {
		socket = new SSHServerSocket();
		this.handler = handler;
	}
	
	//成员内部类
	private class MessageListener implements Runnable{
		
		private Server server; 
		
		public MessageListener(Server server) {
			this.server = server;
		}
		public void run() {
			BufferedReader reader = server.getSocket().getSocketReader();
			String buf;
			try {
				char[] cbuf = new char[1024];
				reader.read(cbuf);
				buf = new String(cbuf);
				while(buf != null && status == STARTED){
					server.getHandler().execute(buf);
					
					cbuf = new char[1024];
					reader.read(cbuf);
					buf = new String(cbuf);
				}
				reader.close(); // 关闭Socket输入流
			} catch (Exception e) {
				logger.info("服务端消息监听线程异常，原因："+e.getMessage());
			}finally{
				server.shutdown();
			}
		}
	}
	

	private class ConnectListener implements Runnable{
		private Server server;
		public ConnectListener(Server server) {
			this.server = server;
		} 
		public void run() {
			while(true){
				try{
				    server.socket.getSocket().sendUrgentData(0xff);
				    Thread.sleep(1000);//1秒
				}catch(Exception e){
					status = UNCONNECTED;
					logger.info("socket连接已断开");
					break;
				}
			}
		}
	}
	
	public void start(){
		socket.accept();
		new Thread(new MessageListener(this)).start();
		logger.info("开始监听客户端输入");
		new Thread(new ConnectListener(this)).start();
		logger.info("开始监听socket连接");
		status = STARTED;
		logger.info("服务端已启动");
	}
	
	public void shell(String shell){
		socket.send(shell);
	}
	public synchronized void shutdown(){
		if(status != SHUTDOWNED){
			if(socket != null){
				socket.close();
			}
			status = SHUTDOWNED;
			logger.info("关闭服务端");
		}
	}
	
	public MessageHandler getHandler() {
		return handler;
	}
	public void setHandler(MessageHandler handler) {
		this.handler = handler;
	}

	public SSHServerSocket getSocket() {
		return socket;
	}

	public void setSocket(SSHServerSocket socket) {
		this.socket = socket;
	}
	
	
}
