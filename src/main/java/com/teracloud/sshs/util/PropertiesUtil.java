package com.teracloud.sshs.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 * 读取配置文件帮助类
 * @author TAES
 * @date 2016年3月21日
 */
public class PropertiesUtil {
	private static Properties p = new Properties();  
	private static boolean isExist = false;
	
	
	public static void load(String path){
		InputStream in = PropertiesUtil.class.getResourceAsStream(path);
        try {  
            p.load(in);  
            in.close();  
            isExist = true;
        } catch (IOException e) {  
            e.printStackTrace();  
        }
	}
	/**
	 * 根据key读取对应的配置信息
	 * @param key
	 * @return  String  
	 * @throws
	 */
	public static String getValue(String key){
		return p.getProperty(key);
	}
	
	/**
	 * 是否存在配置文件
	 * @return
	 */
	public static boolean exist(){
		return isExist;
	}
}
