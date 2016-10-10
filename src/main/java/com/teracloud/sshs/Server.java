package com.teracloud.sshs;


import java.io.BufferedReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.teracloud.sshs.handler.MessageHandler;
import com.teracloud.sshs.handler.impl.SystemOutHandler;
import com.teracloud.sshs.socket.SSHServerSocket;


public class Server {
	private static Logger logger = Logger.getLogger(Server.class);
	
	private SSHServerSocket socket;
	private MessageHandler handler;
	private Server server; 
	
	
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
				while(!Constant.QUIT_SSH.equals(buf)){
					if(buf != null){
						server.getHandler().execute(buf);
					}
					cbuf = new char[1024];
					reader.read(cbuf);
					buf = new String(cbuf);
				}
				reader.close(); // 关闭Socket输入流
			} catch (IOException e) {
				e.printStackTrace();
				try {
					reader.close();
					server.shudown();
				} catch (IOException e1) {} 
			}
		}
	}
	
	
	public void start(){
		socket.accept();
		new Thread(new MessageListener(this)).start();
		logger.info("开始监听终端输出和服务端输入");
	}
	
	public void shell(String shell){
		socket.send(shell);
	}
	public void shudown(){
		socket.send(Constant.QUIT_SSH);
		socket.close();
	}
	
	
	public static void main(String args[]) throws InterruptedException {
		Server server = new Server(new SystemOutHandler());
		server.start();
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

/*
class ShellListener implements Runnable{
	private PrintWriter os ;
	private Server server;
	
	public ShellListener(Server server) {
		this.server = server;
		this.os = os;
	}

	public void run() {
		BufferedReader sin = new BufferedReader(new InputStreamReader(
										System.in));
		try {
			String line = sin.readLine();
			while(!Constant.QUIT_SSH.equals(line)){
				os.println(line);
				os.flush();
				line = sin.readLine();
			}
			os.close();
			System.out.println("关闭连接");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}*/
