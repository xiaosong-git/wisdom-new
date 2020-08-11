package com.xdream.wisdom.util.signature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Soap
 * 不良签名
 */
public class BLsignature {
	public static String sign(Map<String, String> maps) {
		String result = "";
		try {
			List<Map.Entry<String, String>> info = new ArrayList<Map.Entry<String, String>>(maps.entrySet());
			// 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
//			Collections.sort(info, new Comparator<Map.Entry<String, String>>() {
//				public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
//					return (o1.getKey()).compareTo(o2.getKey());
//				}
//			});
			StringBuilder sb =new StringBuilder();
			for(Map.Entry<String, String> item:info) {
				if(item.getKey()!=null&&item.getKey()!="") {
					String key=item.getKey();
					String value=item.getValue();
					if(value!=null&&value!="") {
						sb.append(key+"="+value+"&");
					}
				}
			}
			result=sb.toString();
		} catch (Exception e) {
			return null;
		}
		return result;
	}
}
