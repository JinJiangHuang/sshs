package com.teracloud.sshs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import com.teracloud.sshs.conf.ClientConfigure;
import com.teracloud.sshs.exception.ClientStartException;
import com.teracloud.sshs.socket.SSHClientSocket;
import com.teracloud.sshs.terminal.Terminal;
/**
 * 
 * @author teraee
 *
 */
public class Client {
	//Client status
	public static final int STARTED = 1;
	public static final int WAITTING = 0;
	public static final int SHUTDOWNED = -1;
	
	private static Logger logger = Logger.getLogger(Client.class);
	
	private Terminal terminal;
	private ClientConfigure conf;
	private SSHClientSocket socket;
	
	private int status = WAITTING;
	
	public Client(ClientConfigure conf) {
		this.conf = conf;
		logger.info("客户端配置："+conf);
	}
	/**
	 * 启动客户端。
	 * 与服务端建立socket连接后开启Terminal。
	 * 启动监听线程监听终端输入和输出。
	 * 设置客户端状态为 STARTED
	 * @throws ClientStartException
	 */
	public void start() throws ClientStartException{
		logger.info("启动客户端");
		try {
			socket = new SSHClientSocket(conf.getSocketHost(),conf.getSocketPort());
			socket.connect();
		
			terminal = new Terminal(conf.getSshHost(), conf.getSshPort(), conf.getSshUsr(),conf.getSshPwd());
			terminal.open();
			
			new Thread(new MessageListener(this)).start();
			logger.info("终端输入监听线程启动");
			new Thread(new ResultListener(this)).start();
			logger.info("终端输出监听线程启动");
			
			logger.info("客户端启动完成");
			
			status = STARTED;
		} catch (Exception e) {
			e.printStackTrace();
			socket.send("\n"+Constant.OPEN_TERMINAL_FAILURE+"\n");
			shutdown();
			logger.error("客户端启动失败，已关闭!!!");
			throw new ClientStartException();
		}
	}
	/**
	 * 关闭客户端。
	 * 会关闭SSHClientSocket和Terminal,并设置客户端状态为 SHUTDOWNED
	 */
	public synchronized void shutdown(){
		if(status == STARTED){
			if(socket != null){
				socket.close();
			}
			if(terminal != null){
				terminal.close();
			}
			status = SHUTDOWNED;
			logger.info("关闭客户端");
		}
	}
	
	private class MessageListener implements Runnable{
		
		private Client client;
		private Terminal terminal;
		private BufferedReader is;
		
		public MessageListener(Client client) {
			this.client = client;
			this.is = client.socket.getSocketReader();
			this.terminal = client.terminal;
		}
		public void run() {
			try {
				char[] cbuf = new char[1];
				is.read(cbuf);
				String buf = new String(cbuf);
				
				while(!Constant.QUIT_SSH.equals(buf) 
						&& buf != null && client.status == STARTED){
					terminal.in(buf);
					cbuf = new char[1];
					is.read(cbuf);
					buf = new String(cbuf);
				}
			} catch (IOException e) {
				logger.info("客户端消息监听进程异常："+e.getMessage());
			}finally{
				client.shutdown();
				logger.info("客户端消息监听进程关闭");
			}
		}
	}

	private class ResultListener implements Runnable{
		private PrintWriter os;
		private Terminal terminal;
		private Client client;
		
		public ResultListener(Client client) {
			this.client = client;
			this.os = client.socket.getSocketWriter();
			this.terminal = client.terminal;
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
				
				while(buf != null  && client.status == STARTED){
					os.write(buf);
					os.flush();
					cbuf = new char[1024];
					reader.read(cbuf);
					buf = new String(cbuf);
				}
			} catch (IOException e) {
				logger.info("客户端结果监听进程异常："+e.getMessage());
			}finally{
				client.shutdown();
				logger.info("客户端结果监听线程关闭");
			}
		}
	}
	
	
}



