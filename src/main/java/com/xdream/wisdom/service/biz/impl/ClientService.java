package com.xdream.wisdom.service.biz.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xdream.wisdom.config.Parameters;
import com.xdream.wisdom.service.biz.IChannelService;
import com.xdream.wisdom.service.biz.IClientService;
import com.xdream.wisdom.service.biz.ICustomerService;
import com.xdream.wisdom.util.response.ResultData;
@Service("clientService")
public class ClientService implements IClientService {

	@Resource(name="customerService")
	private ICustomerService customerService;
	@Resource(name="channelService")
	private IChannelService channelService;
	@Override
	public String clientMsgSer(Map<String,String> msgHead, Map<String,String> msgBody) {
		String ResultMap="";
		try {
			ResultMap =  customerService.checkReqMsg(msgHead,msgBody);
			//请求报文校验不通过
			JSONObject resultMapjs=JSON.parseObject(ResultMap);
			if(Parameters.ERR_FLAG.equals((resultMapjs.get("succ_flag")))){
				return ResultMap;
			}else{
				//平台产品对应通道请求校验
				ResultMap =channelService.InChannel(msgHead, msgBody);
			}
		} catch (Exception e) {
			ResultMap =ResultData.defaultResultMap("8", msgHead.get("serialno"));
			e.printStackTrace();
		}
		return ResultMap;
	}

}
