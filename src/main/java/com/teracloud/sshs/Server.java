package com.teracloud.sshs;


import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.teracloud.sshs.conf.ServerConfigure;
import com.teracloud.sshs.exception.ServerConfigurationIllegalParamException;
import com.teracloud.sshs.exception.ServerNoneFreePortException;
import com.teracloud.sshs.handler.MessageHandler;
import com.teracloud.sshs.handler.impl.SystemOutHandler;
import com.teracloud.sshs.socket.SSHServerSocket;
import com.teracloud.sshs.util.PropertiesUtil;


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
	
	private static ServerConfigure conf;
	
	static{
		//加载配置文件
		try {
			loadConf();
		} catch (ServerConfigurationIllegalParamException e) {
			e.printStackTrace();
		}
	}
	
	
	public Server(MessageHandler handler) throws ServerNoneFreePortException{
		if(conf.isLimitPort()){
			for(Port p : conf.getOpenPorts()){
				try {
					new ServerSocket(p.getPort()).close();
					socket = new SSHServerSocket(p.getPort());
					logger.info("端口[" +p.getPort() + "]可用。");
					break;
				} catch (IOException e) {}
			}
			
			if(socket == null){
				throw new ServerNoneFreePortException();
			}
		}else{
			socket = new SSHServerSocket();
		}
		this.handler = handler;
	}
	
	//成员内部类
	private class MessageListener implements Runnable{
		
		private Server server; 
		
		public MessageListener(Server server) {
			this.server = server;
		}
		public void run() {
			logger.info("客户端输入监听器等待客户端响应 ");
			while(status == WAITTING){ 
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {}
			}
			logger.info("开始监听客户端输入");
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
	
	private class ClientListener implements Runnable{
    	private Server server;
    	public ClientListener(Server server){
    		this.server = server;
    	}
    	
		@Override
		public void run() {
			logger.info("开始监听客户端响应");
			status = WAITTING;
			server.socket.accept();//此处会堵塞
			status = STARTED;
			logger.info("服务端已启动");
		}
    }
	
	private class ConnectListener implements Runnable{
		private Server server;
		public ConnectListener(Server server) {
			this.server = server;
		} 
		public void run() {
			logger.info("socket连接监听器等待客户端响应");
			while(status == WAITTING){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {}
			}
			logger.info("开始监听socket连接");
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
		
		new Thread(new ClientListener(this)).start();
		
		new Thread(new MessageListener(this)).start();
		
		new Thread(new ConnectListener(this)).start();
	}
	
	public void startBlock(){
		
		logger.info("开始监听客户端响应");
		status = WAITTING;
		socket.accept();//此处会堵塞
		status = STARTED;
		new Thread(new MessageListener(this)).start();
		new Thread(new ConnectListener(this)).start();
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
	
	private static void loadConf() throws ServerConfigurationIllegalParamException{
		logger.info("加载服务端配置");
		PropertiesUtil.load("/sshs.properties");
		conf = new ServerConfigure();
		if(PropertiesUtil.exist()){
			String isLimitPort =  PropertiesUtil.getValue("isLimitPort");
			String openPorts = PropertiesUtil.getValue("openPorts");
			
			if(StringUtils.isNotBlank(isLimitPort)){
				Boolean ilp = Boolean.valueOf(isLimitPort);
				//开启端口限制 
				if(ilp){
					logger.info("开启端口限制");
					int[] ports = getOpenPort(openPorts);
					
					Port[] pts = new Port[ports.length];
					for(int i= 0;i < pts.length;i++){
						Port p = new Port(ports[i]);
						pts[i] = p;
					}
					conf.setOpenPorts(pts);
					logger.info("开放端口为："+openPorts);
				}else{
					logger.info("不限制端口");
				}
				conf.setLimitPort(ilp);
				logger.info("服务端配置加载成功");
			}
		}else{
			logger.error("未找到服务端配置文件 ，服务端使用默认配置");
		}
	}
	
	private static int[] getOpenPort(String str) 
			throws ServerConfigurationIllegalParamException{
		int[] ps = new int[0];
		try{
			List<Integer> ports = new ArrayList<Integer>();
			//先按 逗号分割
			String[] splits = str.split(",");
			for(String tmp : splits){
				//使用  xxxx-xxx 区间格式
				if(tmp.contains("-")){
					String[] splits2 = tmp.split("-");
					int begin = Integer.valueOf(splits2[0]);
					int end = Integer.valueOf(splits2[1]);
					for(int i = begin; i <= end;i++){
						ports.add(i);
					}
				}else{
					ports.add(Integer.valueOf(tmp));
				}
			}
			ps = new int[ports.size()];
			for(int i = 0;i < ps.length;i++){
				ps[i] = ports.get(i);
			}
			
		}catch(Exception e){
			throw new ServerConfigurationIllegalParamException();
		}
		return ps;
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
	
	public static void main(String[] args) throws ServerConfigurationIllegalParamException, InterruptedException, ServerNoneFreePortException {
		/*int[] ports = Server.getOpenPort("11,111-222");
		for(int p : ports){
			
			System.out.println(p);
		}*/
		
		Server server = new Server(new SystemOutHandler());
		
		Thread.sleep(1000000);
	}
	
}
