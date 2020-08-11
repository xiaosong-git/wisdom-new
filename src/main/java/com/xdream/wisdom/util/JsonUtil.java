package com.xdream.wisdom.util;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xdream.wisdom.config.Parameters;


/**
 * json遍历类
 * @author Administrator
 *
 */
public class JsonUtil {
	
	/**
	 * 遍历获取json所有字段
	 * @param returnMap
	 * @param jsinStr
	 * @return
	 */
	public static Map<String,String> listJsonStr(Map returnMap,JSONObject jsonStr){
		Iterator<String> iterator = jsonStr.keySet().iterator();
		while (iterator.hasNext()) {
			String keyString =iterator.next();
			String valueString="";
			try {
				valueString = jsonStr.getString(keyString);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			int i=testJSonObject(valueString);//0-非json串，1-json
			 if(i==2){
				 JSONArray arrays;
				try {			
					arrays = JSON.parseArray(valueString);
					if(arrays!=null) {
					 for(int k =0;k<arrays.size();k++){
						 JSONObject array = (JSONObject)arrays.get(k);
						 returnMap = listJsonStr(returnMap,array);
					 }
					}
					else {
						if(returnMap.containsKey(keyString)){
							keyString=keyString+"_"+1;
						}
						returnMap.put(keyString, valueString);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			 }else if(i==1){
				 JSONObject jsobj;
					try {
						jsobj = JSON.parseObject(valueString);
						returnMap = listJsonStr(returnMap,jsobj);
							
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
		}else if(i==0){
			if(returnMap.containsKey(keyString)){
				keyString=keyString+"_"+1;
			}
			returnMap.put(keyString, valueString);
			
			}
		}
		return returnMap;
	}
	
	/**
	 * 判断
	 * @param str
	 * @return
	 */
	public static int testJSonObject(String str){
		try {
			JSONArray array =JSON.parseArray(str);
			return 2;
		} catch (Exception e) {// 抛错 说明JSON字符不是数组或根本就不是JSON
			try {
				JSONObject object = JSON.parseObject(str);
				return 1;
			} catch (Exception e2) {// 抛错 说明JSON字符根本就不是JSON
				return 0;
			}
		}
	}
	
	/**
	 * 新增流水jsonstr报文拼接
	 * @param msgHead
	 * @param msgbody
	 * @return json{"actiontype":"insert","msg_trans":{各个字段详细信息}}
	 */
	public static String packJsonInsertTran(Map<String, String> msgHead,Map<String, String> msgbody){
		JSONObject jons_msg=new JSONObject();
		JSONObject jons_trans=new JSONObject();
		
		String serialno =msgHead.get("serialno");
		String custid=msgHead.get("custid");
		String productcode=msgHead.get("productcode");
		//把msgbody统一往msgHEAD里压
		 for (String key : msgbody.keySet()) {
			 msgHead.put(key, msgbody.get(key));
			  }

		 if(msgHead.containsKey("photo")){
		 	Iterator<String> iterator=msgHead.keySet().iterator();
		 	while (iterator.hasNext()){
				String key = iterator.next();
				if("photo".equals(key)){
					iterator.remove();
				}
			}
		 }

		String jons_requestdata=JSON.toJSONString(msgHead);
		jons_trans.put("serialno", serialno);
		jons_trans.put("custid", custid);
		jons_trans.put("custacct", custid+productcode);
		jons_trans.put("transdate", DateUtil.getCurDate());
		jons_trans.put("amt", null);
		jons_trans.put("productcode", productcode);
		jons_trans.put("succ_flag", null);
		jons_trans.put("remark", null);
		jons_trans.put("requestdata", jons_requestdata);
		jons_trans.put("responsedata", null);
		jons_trans.put("responsetime", null);
		String msg_trans=jons_trans.toJSONString();
		jons_msg.put("actiontype", Parameters.ACTION_INSERT);
		jons_msg.put("msg_trans", msg_trans);
		return jons_msg.toJSONString();
	}
	
	/**
	 * json{"actiontype":"update","msg_trans":{"transls"},"msg_custacct":{"custacct"}}}
	 * @param msgHead
	 * @param response
	 * @param cost
	 * @return
	 */
	public static String packJsonUpdateTran(Map<String,String> msgHead,Map<String,String> response,BigDecimal cost){
		JSONObject jons_msg=new JSONObject();
		JSONObject jons_trans=new JSONObject();
		JSONObject jons_acct=new JSONObject();
		jons_trans.put("serialno", msgHead.get("serialno"));
		jons_trans.put("custid", msgHead.get("custid"));
		jons_trans.put("responsedata", JSON.toJSONString(response));
		jons_trans.put("succ_flag", response.get("succ_flag"));
		jons_trans.put("responsetime", DateUtil.getSystemDateTime());
		String msg_trans=jons_trans.toJSONString();
		
		jons_acct.put("custacct", msgHead.get("custid")+msgHead.get("productcode"));
		jons_acct.put("amt", cost);
		String msg_custacct=jons_acct.toJSONString();
		jons_msg.put("actiontype", Parameters.ACTION_UPDATE);
		jons_msg.put("msg_trans", msg_trans);
		jons_msg.put("msg_custacct", msg_custacct);
		return jons_msg.toJSONString();
	}


	public static void main(String[] args)throws Exception{
//		String ss="{\"info\":[{\"goodsId\":\"1234\",\"goodsq\":\"10\"},{\"goodsId\":\"5678\",\"goodsq\":\"20\"}]}";
//		JSONObject jsonObject=new JSONObject(ss);
//		Map<String,String> aa =new HashedMap<>();
//		Map<String,String> cc =new HashedMap<>();
//		 cc=listJsonStr(aa, jsonObject);
//		 Iterator st= cc.entrySet().iterator();
//		 while(st.hasNext()){
//			 Map.Entry entry1 = (Map.Entry) st.next();
//			 Object key=entry1.getKey();
//			 Object value =entry1.getValue();
//			 System.out.println("key="+key+",value="+value);
//		 }
		
	}
}
