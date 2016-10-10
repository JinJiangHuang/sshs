package com.teracloud.sshs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSchException;
import com.teracloud.sshs.conf.ClientConfigure;
import com.teracloud.sshs.exception.ClientStartException;
import com.teracloud.sshs.exception.SocketConnectException;
import com.teracloud.sshs.socket.SSHClientSocket;
import com.teracloud.sshs.terminal.Terminal;

public class Client {
	private static Logger logger = Logger.getLogger(Client.class);
	
	private Terminal terminal;
	private ClientConfigure conf;
	private SSHClientSocket socket;
	
	public Client(ClientConfigure conf) {
		this.conf = conf;
		logger.info("客户端配置："+conf);
	}
	
	public void start() throws ClientStartException{
		try {
			socket = new SSHClientSocket(conf.getSocketHost(),conf.getSocketPort());
			socket.connect();
			BufferedReader socketReader = socket.getSocketReader();
			PrintWriter socketWriter = socket.getSocketWriter();
			terminal = new Terminal(conf.getSshHost(), conf.getSshPort(), conf.getSshUsr(),conf.getSshPwd());
			terminal.open();
			logger.info("启动终端...");
			new Thread(new MessageListener(socketReader,terminal)).start();
			logger.info("终端输入监听线程启动...");
			new Thread(new ResultListener(socketWriter,terminal)).start();
			logger.info("终端输出监听线程启动...");
			logger.info("终端启动完成<<");
		} catch (Exception e) {
			e.printStackTrace();
			socket.send("\n"+Constant.OPEN_TERMINAL_FAILURE+"\n");
			shutdown();
			logger.error("终端启动失败!!!");
			throw new ClientStartException();
		}
	}
	
	public void shutdown(){
		if(socket != null){
			socket.close();
		}
		if(terminal != null){
			terminal.close();
		}
	}
	
	
	
	private class MessageListener implements Runnable{
		
		private BufferedReader is;
		private Terminal terminal;
		
		
		public MessageListener(BufferedReader is,Terminal terminal) {
			this.is = is;
			this.terminal = terminal;
		}
		public void run() {
			try {
				char[] cbuf = new char[1];
				is.read(cbuf);
				String buf = new String(cbuf);
				
				while(!Constant.QUIT_SSH.equals(buf)){
					socket.getSocket().sendUrgentData(0xFF);
					logger.info("终端输入："+buf);
					if(buf == null){
						break;
					}
					
					/*buf.replace("\0", "");*/
					terminal.in(buf);
					
					cbuf = new char[1];
					is.read(cbuf);
					buf = new String(cbuf);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				logger.info("client message listerner stop");
				try {
					if(is != null){is.close();}
				} catch (IOException e) {
					e.printStackTrace();
				}
				socket.close();
				terminal.close();
			}
		}
	}

	class ResultListener implements Runnable{
		private PrintWriter os;
		private Terminal terminal;
		public ResultListener(PrintWriter os,Terminal terminal) {
			this.os = os;
			this.terminal = terminal;
		}
		public void run() {
			String buf ;
			BufferedReader reader;
			
			try {
				InputStream resultStream = terminal.getShellOutReciver();
				reader = new BufferedReader(new InputStreamReader(resultStream));
								
				char[] cbuf = new char[1024];
				reader.read(cbuf);
				buf = new String(cbuf);
				
				while(buf != null){
					os.write(buf);
					os.flush();
					cbuf = new char[1024];
					reader.read(cbuf);
					buf = new String(cbuf);
				}
			} catch (IOException e) {
				e.printStackTrace();
				
			}finally{
				logger.info("client result listerner stop");
				if(os != null){
					os.close();
				}
				socket.close();
				terminal.close();
			}
		}
	}
	
	
}



