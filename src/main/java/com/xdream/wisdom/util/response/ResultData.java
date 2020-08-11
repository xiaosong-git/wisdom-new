package com.xdream.wisdom.util.response;

import java.util.HashMap;
import java.util.Map;

import com.aliyun.openservices.shade.io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class ResultData {
	//公共返回头
	public static String resultMap(String succ_flag,String return_code,String ret_plain,String serialno){
		
		Map<String, String>resultMap=new HashMap<String, String>();
		resultMap.put("succ_flag", succ_flag);
		resultMap.put("return_code", return_code);
		resultMap.put("ret_plain", ret_plain);
		resultMap.put("serialno", serialno);
		String resJson=JSON.toJSONString(resultMap);
		return resJson;
	}
	/**
	 * 返回错误码定义
	 * @param flag
	 * @param serialno
	 * @return
	 */
	public static String defaultResultMap(String flag,String serialno){
		String resjsonString="";
		if(!StringUtils.isBlank(flag)){
			String succ_flag="";
			String return_code="";
			String ret_plain="";
			switch (flag) {
			case "1":
				succ_flag="1";
				return_code="0001";
				ret_plain ="请求报文头信息不全";
				break;
			case "2":
				succ_flag="1";
				return_code="0002";
				ret_plain ="商户不存在";
				break;
			case "3":
				succ_flag="1";
				return_code="0003";
				ret_plain ="商户状态异常";
				break;
			case "4":
				succ_flag="1";
				return_code="0004";
				ret_plain ="业务编号不存在或商户未开通改业务";
				break;
			case "5":
				succ_flag="1";
				return_code="0005";
				ret_plain ="账户状态异常";
				break;
			case "6":
				succ_flag="1";
				return_code="0006";
				ret_plain ="账户余额不足";
				break;
			case "7":
				succ_flag="1";
				return_code="0007";
				ret_plain ="mac校验失败";
				break;
			case "8":
				succ_flag="1";
				return_code="0008";
				ret_plain ="系统异常";
				break;
			default:
				succ_flag="0";
				return_code="0000";
				ret_plain ="交易成功";
				break;
			}
			resjsonString =resultMap(succ_flag,return_code,ret_plain,serialno);
		}
		return resjsonString;
	}

	public static Map<String, String>resultMap(String returnCode,String message,String data){

		Map<String, String>resultMap=new HashMap<String, String>();

		resultMap.put("success", returnCode);
		resultMap.put("data", data);
		resultMap.put("errors", message);

		return resultMap;
	}
	//要素返回参数
	public static Map<String, String>resultMap(String returnCode,String message,String merchOrderId,String bankResult,String faceComparisonScore){

		Map<String, String>resultMap=new HashMap<String, String>();

		resultMap.put("returnCode", returnCode);
		resultMap.put("message", message);
		resultMap.put("merchOrderId", merchOrderId);
		resultMap.put("bankResult",bankResult);
		if(!StringUtil.isNullOrEmpty(faceComparisonScore)||!"null".equals(faceComparisonScore)){
			resultMap.put("faceComparisonScore",faceComparisonScore);
		}
		return resultMap;
	}
	//要素返回参数
	public static Map<String, String>resultMapPhoto(String returnCode,String message,String merchOrderId,String bankResult,String photo){

		Map<String, String>resultMap=new HashMap<String, String>();
		resultMap.put("returnCode", returnCode);
		resultMap.put("message", message);
		resultMap.put("merchOrderId", merchOrderId);
		resultMap.put("bankResult",bankResult);
		resultMap.put("photo",photo);
		return resultMap;
	}

	//翰迪不良信息查询
	public static Map<String, Object>badInfoResult(String returnCode,String message,String merchOrderId,String resData){

		Map<String, Object>resultMap=new HashMap<String, Object>();
		resultMap.put("returnCode", returnCode);
		resultMap.put("message", message);
		resultMap.put("merchOrderId", merchOrderId);
		resultMap.put("resData",resData);
		return resultMap;
	}
	//翰迪、极推 运营商三要素验证
	public static Map<String, String>phoneResult(String returnCode,String message,String merchOrderId){

		Map<String, String>resultMap=new HashMap<String, String>();
		resultMap.put("returnCode", returnCode);
		resultMap.put("message", message);
		resultMap.put("merchOrderId", merchOrderId);
		return resultMap;
	}
	//极推在网时长
	public static Map<String, Object>phoneResult(String returnCode,String message,String merchOrderId,String result){

		Map<String, Object>resultMap=new HashMap<String, Object>();
		resultMap.put("returnCode", returnCode);
		resultMap.put("message", message);
		resultMap.put("merchOrderId", merchOrderId);
		resultMap.put("result", result);
		return resultMap;
	}
	
}
