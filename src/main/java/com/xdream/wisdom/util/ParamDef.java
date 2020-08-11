package com.xdream.wisdom.util;

import java.io.InputStream;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ParamDef {

	
	protected static Properties JinDieConfig = null;//金蝶
	
	
	//金蝶
	public static String findJDByName(String key){
		String value = null;
		try{
			if (JinDieConfig==null){
				//载入
				JinDieConfig = new Properties();
	            Resource resource = new ClassPathResource("jindie.properties");
	        	InputStream in = resource.getInputStream();//ClassLoader.getSystemResourceAsStream(propPath);
	        	JinDieConfig.load(in);
			}
			value = JinDieConfig.getProperty(key);
		}catch(Exception e){
			e.printStackTrace();			
		}finally{
			return value;
		}
	}
	
}
