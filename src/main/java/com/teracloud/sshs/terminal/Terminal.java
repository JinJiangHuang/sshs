package com.teracloud.sshs.terminal;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class Terminal {
	
	private static Properties config = new Properties();
	private static int timeout = 60000000;
	private static Logger logger = Logger.getLogger(Terminal.class);
	static{
		config.put("StrictHostKeyChecking", "no");
	}
	
	private Session session;
	
	private String userName;
	private String host;
	private String password;
	private int port;
	
	private PipedOutputStream shellInReciver;
	private PipedInputStream shellOutReciver;
	
	private static final byte[] kEY_ENTER= {(byte)0x0d};
	private static final byte[] KEY_TAB = {(byte)0x09};
	private static final byte[] KEY_TAB_DOUBLE = {(byte)0x09,(byte)0x09};
	private static final byte[] KEY_UP = {(byte)0x1b, (byte)0x4f, (byte)0x41};
	private static final byte[] KEY_DOWN = {(byte)0x1b, (byte)0x4f, (byte)0x42};
	private static final byte[] KEY_RIGHT = {(byte)0x1b, (byte)/*0x5b*/0x4f, (byte)0x43};
	private static final byte[] KEY_LEFT = {(byte)0x1b, (byte)/*0x5b*/0x4f, (byte)0x44};
	
	public Terminal(String host,int port,String userName,String password) {
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}
	
	public void open() throws JSchException, IOException{
		JSch jsch = new JSch(); // 创建JSch对象
		session = jsch.getSession(userName, host, port); // 根据用户名，主机ip，端口获取一个Session对象
		session.setPassword(password); // 设置密码		
		session.setConfig(config); // 为Session对象设置properties	
		session.setTimeout(timeout); // 设置timeout时间
		session.connect(); // 通过Session建立链接
		
		PipedInputStream shellIn = new PipedInputStream(); 	
		shellInReciver = new PipedOutputStream();
		shellIn.connect(shellInReciver);
			
		PipedOutputStream shellOut = new PipedOutputStream();  
		shellOutReciver = new PipedInputStream(); 	
		shellOut.connect(shellOutReciver);
		
		shell(shellIn,shellOut);
		logger.info("终端已启动");
		
	}
	
	public void in(String cmd){
		try {
			shellInReciver.write(cmd.getBytes("utf-8"));
			shellInReciver.flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public String out() throws IOException{
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(shellOutReciver));
		
		char[] chrs = new char[1024];
		reader.read(chrs);
		
		return new String(chrs);
	}
	
	//按键输入
	public void enter() throws JSchException, IOException{
		shellInReciver.write(kEY_ENTER);
	}
	public void up() throws IOException{
		shellInReciver.write(KEY_UP);
	}
	public void down() throws IOException{
		shellInReciver.write(KEY_DOWN);
	}
	public void left() throws IOException{
		shellInReciver.write(KEY_LEFT);
	}
	public void right() throws IOException{
		shellInReciver.write(KEY_RIGHT);
	}
	public void tab() throws IOException{
		shellInReciver.write(KEY_TAB);
	}
	public void tabDouble() throws IOException{
		shellInReciver.write(KEY_TAB_DOUBLE);
	}
	public void close(){
		if(shellInReciver != null){
			try {
				shellInReciver.close();
			} catch (IOException e) {}
		}
		if(shellOutReciver != null){
			try {
				shellOutReciver.close();
			} catch (IOException e) {}
		}
		if (null != session) {
			session.disconnect();
		}
	}
	public void shell(InputStream in,OutputStream out) throws JSchException{
		 ChannelShell channelShell = (ChannelShell)session.openChannel( "shell" );  
		 channelShell.setInputStream(in);  
	     channelShell.setOutputStream(out); 
	     channelShell.connect( timeout );
	}
	
	
	
	
	
	
	public static void main(String[] args) throws Exception {
	/*	Terminal terminal = new Terminal("localhost", 22, "teraee", "123456");
		terminal.open();
		Thread.sleep(1000);
		terminal.in("vi hello");
		terminal.enter();
		Thread.sleep(2000);
		System.out.println(terminal.out());
		
		terminal.enter();
		Thread.sleep(2000);
		System.out.println(terminal.out());
		
		terminal.enter();
		terminal.down();
		Thread.sleep(2000);
		System.out.println(terminal.out());*/
		
		
	}

	public PipedOutputStream getShellInReciver() {
		return shellInReciver;
	}

	public PipedInputStream getShellOutReciver() {
		return shellOutReciver;
	}
	
}
