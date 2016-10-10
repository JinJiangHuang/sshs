package com.teracloud.sshs.socket;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

public abstract class SSHSocket {
	
	private static Logger logger = Logger.getLogger(SSHSocket.class);
	
	protected int port;
	protected Socket socket;
	protected PrintWriter socketWriter;
	protected BufferedReader socketReader;
	
	public void send(String message){
		socketWriter.write(message);
		socketWriter.flush();
	}	
	
	public void close(){
		logger.info("关闭客户端socket连接");
		if(socketWriter != null){
			socketWriter.close();
		}
		if(socketReader != null){
			try {
				socketReader.close();
			} catch (IOException e) {}
		}
		if(socket != null){
			try {
				socket.shutdownOutput();
				socket.shutdownInput();
				socket.close();
			} catch (IOException e) {}
		}
	}
	
	public int getPort() {
		return port;
	}

	public PrintWriter getSocketWriter() {
		return socketWriter;
	}

	public BufferedReader getSocketReader() {
		return socketReader;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	
}
