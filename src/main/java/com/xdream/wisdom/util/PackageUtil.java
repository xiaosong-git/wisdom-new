package com.xdream.wisdom.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;

/**
 * 拆包 拼包
 * @author Administrator
 *
 */
public class PackageUtil {
	
	
	public static final String[] MSGHEADER ={"custid","txcode","productcode","serialno","mac"};
	
	/**
	 * 公共报文头拆包
	 * @param packStr
	 * @return
	 */
	public static Map unPackageHeader(String packStr){
		if(packStr==null||packStr=="") {
			return null;
		}
		//res是json字符串转化后的map，map是最终返回的map
		Map<String,Object>  res = new HashMap<String,Object>();
		Map<String,Object>  map = new HashMap<String,Object>();
		//parseObjcet将json字符串转成map
		try {
 		res=JSON.parseObject(packStr);
		}catch(Exception e){
			return null;
		}
		for(int i=0;i<MSGHEADER.length;i++){
			map.put(MSGHEADER[i], res.get(MSGHEADER[i]));
		}
		return map;
	}
	
	
	
	/**
	 * 报文体拆包拆包 请求报文扣除报文头
	 * @param packStr
	 * @return
	 */
	public static Map unPackageBody(String packStr){
		if(packStr==null||packStr=="") {
			return null;
		}
		//res是json字符串转化后的map，map是最终返回的map
		Map<String,Object>  res = new HashMap<String,Object>();
		//parseObjcet将json字符串转成map
 		res=JSON.parseObject(packStr);
 		try {
 	 		res=JSON.parseObject(packStr);
 			}catch(Exception e){
 				return null;
 			}
 		for(int i=0;i<MSGHEADER.length;i++){
 			res.remove(MSGHEADER[i]);
		}
 		return res;
	}
	
	
	/**
	 * 公共拼包
	 * @param packString
	 * @return
	 */
	public Object packAge(String packString){
		return null;
	}

}
