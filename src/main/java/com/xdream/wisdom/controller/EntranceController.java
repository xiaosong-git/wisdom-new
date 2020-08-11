package com.xdream.wisdom.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.xdream.wisdom.customers.allinpay.AllinpayConfig;
import com.xdream.wisdom.customers.allinpay.ResponseData;
import com.xdream.wisdom.customers.allinpay.ResultCode;
import com.xdream.wisdom.customers.allinpay.SignUtils;
import com.xdream.wisdom.util.encryption.MD5Util;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.xdream.wisdom.service.biz.IClientService;
import com.xdream.wisdom.util.PackageUtil;

/**
 * 下游所有平台入口
 * @author Administrator
 *
 */
@RequestMapping(value = "/entrance")
@Controller
@ResponseBody
public class EntranceController extends BaseController {
	
	@Resource(name="clientService")
	private IClientService clientService;


	private SignUtils signUtils = new SignUtils("/project/allinpay/xiaosong.pfx","xiaosong2020");

	@RequestMapping(value = "/pub")
	public String requestPackage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> paramsMap = getParamsToMap(request);
		JSONObject jsonObject=null;
		if(paramsMap.size()>0){
			jsonObject = JSONObject.parseObject(JSON.toJSONString(paramsMap));
		}else {
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
			StringBuilder responseStrBuilder = new StringBuilder();
			String inputStr;
			while ((inputStr = streamReader.readLine()) != null){
				responseStrBuilder.append(inputStr);
			}
			jsonObject = JSONObject.parseObject(responseStrBuilder.toString());
			streamReader.close();
		}
		return parsing(jsonObject);
	}


	/**
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/allinpay")
	public String requestAllinpayPackage(HttpServletRequest request, HttpServletResponse response){
		JSONObject jsonObject=null;
		ResponseData responseData = new ResponseData();
		try {
			Map<String, Object> paramsMap = getParamsToMap(request);
			if (paramsMap.size() > 0) {
				jsonObject = JSONObject.parseObject(JSON.toJSONString(paramsMap));
			} else {
				BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
				StringBuilder responseStrBuilder = new StringBuilder();
				String inputStr;
				while ((inputStr = streamReader.readLine()) != null) {
					responseStrBuilder.append(inputStr);
				}
				jsonObject = JSONObject.parseObject(responseStrBuilder.toString());
				streamReader.close();
			}
			System.out.println("controller 接收到通联云报文：-------" + jsonObject.toJSONString());
			JSONObject jsonBizCont = jsonObject.getJSONObject("bizContent");

			//通联云的报文添加通联云的商户号和秘钥
//			String custid = AllinpayConfig.getInstance().custid;
//			String secret = AllinpayConfig.getInstance().secret;
//			String productcode = jsonBizCont.getString("productcode");
//			String orderId = String.valueOf(System.currentTimeMillis()) + new Random().nextInt(10);
//
//			StringBuilder sb = new StringBuilder();
//			sb.append(custid).append(productcode).append(orderId).append(secret);
//			String newSign = MD5Util.MD5Encode(sb.toString(), "UTF-8");
//			jsonBizCont.put("custid", custid);
//			jsonBizCont.put("serialno", orderId);
//			jsonBizCont.put("mac", newSign);

			String resultData = parsing(jsonBizCont);

			JSONObject result = JSONObject.parseObject(resultData);
			if("00000".equals(result.getString("return_code"))) {
				System.out.println("-------------接口调用成功-----------");
				responseData.setCode(ResultCode.SUCCESS.getCode());
				responseData.setMsg(ResultCode.SUCCESS.getName());
				responseData.setData(resultData);
			}
			else
			{
				System.out.println("-------------接口调用失败-----------");
				throw new Exception(result.getString("data"));
			}
		}
		catch (Exception ex)
		{
			System.out.println("-------------接口调用失败，抛出异常"+ex.getMessage()+"-----------");
			responseData.setMsg(ResultCode.FAIL.getName());
			responseData.setCode(ResultCode.FAIL.getCode());
			responseData.setData(ex.getMessage());
		}
		String sign = signUtils.sign(JSONObject.toJSONString(responseData));
        responseData.setSign(sign);
        return JSONObject.toJSONString(responseData);

	}

	/**
	 * 解析请求报文
	 * @param jsonObject
	 * @return
	 * @throws IOException
	 */
	private String parsing(JSONObject jsonObject) throws IOException
	{
		String reqData= null;
		if("0012".equals(jsonObject.get("productCode"))||"0002".equals(jsonObject.get("productCode")) ||"0003".equals(jsonObject.get("productCode")) ||"0009".equals(jsonObject.get("productCode"))){
			JSONObject obj=new JSONObject();
			obj.put("custid",jsonObject.get("merchantNo"));
			obj.put("txcode","tx00011");
			obj.put("productcode",jsonObject.get("productCode"));
			obj.put("serialno",jsonObject.get("merchOrderId"));
			obj.put("mac",jsonObject.get("sign"));
			obj.put("userName",jsonObject.get("userName"));
			obj.put("certNo",jsonObject.get("certNo"));
			obj.put("photo",jsonObject.get("photo"));
			reqData=obj.toJSONString();
		}else if("0202".equals(jsonObject.get("productCode")) ||"0203".equals(jsonObject.get("productCode"))){
			JSONObject obj=new JSONObject();
			obj.put("custid",jsonObject.get("merchantNo"));
			obj.put("txcode","tx00012");
			obj.put("productcode",jsonObject.get("productCode"));
			obj.put("serialno",jsonObject.get("merchOrderId"));
			obj.put("mac",jsonObject.get("sign"));
			obj.put("userName",jsonObject.get("userName"));
			obj.put("certNo",jsonObject.get("certNo"));
			obj.put("phone",jsonObject.get("phone"));
			reqData=obj.toJSONString();
		}else if("0201".equals(jsonObject.get("productCode")) ||"W003".equals(jsonObject.get("productCode")) || "W004".equals(jsonObject.get("productCode"))){
			JSONObject obj=new JSONObject();
			obj.put("custid",jsonObject.get("merchantNo"));
			obj.put("txcode","tx00013");
			obj.put("productcode",jsonObject.get("productCode"));
			obj.put("serialno",jsonObject.get("merchOrderId"));
			obj.put("mac",jsonObject.get("sign"));
			obj.put("userName",jsonObject.get("userName"));
			obj.put("certNo",jsonObject.get("certNo"));
			reqData=obj.toJSONString();
		}
		else {
			reqData= jsonObject.toJSONString();
		}
		//公共报文头
		Map msgHeader =PackageUtil.unPackageHeader(reqData);
		//报文体
		Map msgBody =PackageUtil.unPackageBody(reqData);
		//通道
		String requestMap =clientService.clientMsgSer(msgHeader, msgBody);
		System.out.println("controller 最终返回数据：-------"+requestMap);
		return requestMap;
	}

}
