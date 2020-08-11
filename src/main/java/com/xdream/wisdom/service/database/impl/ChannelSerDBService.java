package com.xdream.wisdom.service.database.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.xdream.wisdom.dao.ICustDao;
import com.xdream.wisdom.entity.Cust;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.xdream.kernel.sql.SQLConf;
import com.xdream.wisdom.config.Parameters;
import com.xdream.wisdom.dao.IChannelSerDao;
import com.xdream.wisdom.entity.ChannelSer;
import com.xdream.wisdom.service.database.IChannelSerDBService;
import com.xdream.wisdom.util.RedisUtil;
@Service("channelSerDBService")
public class ChannelSerDBService implements IChannelSerDBService {

	//根据通道查询对应的服务码
	@Resource(name = "channelSerDao")
	private IChannelSerDao channelSerDao;

	@Resource(name = "custDao")
	private ICustDao custDao;
	@SuppressWarnings("finally")
	@Override
	public ChannelSer findSerTxInfo(String channelid,String custid) throws Exception {
		ChannelSer channelSer=null;
		String channelSerJson=null;
		
		String  key=channelid+"_channelSer_normal";

		if("SJVE001".equals(channelid)||"RZYJZ001".equals(channelid)){
			String sql2 = SQLConf.getSql("custInfo", "findmacKey");
			Map<String,Object> macMap=new HashMap<String,Object>();
			macMap.put("custid",custid);
			List<Cust> custList = custDao.find(sql2, macMap);

			String sql = SQLConf.getSql("ChannelSer", "findsercode");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("channelid", channelid);
			List<ChannelSer> list = channelSerDao.find(sql, map);
			if (list != null && !list.isEmpty() && list.size() > 0) {
				 channelSer= list.get(0);
				 channelSer.setMackey(custList.get(0).getAppkey());
				 return channelSer;
			} else {
				System.out.println("通道id不等于SJVE001或者RZYJZ001！");
				return null;
			}
		}

		try {
			channelSerJson= RedisUtil.getStrVal(key, Parameters.REDIS_ONE);
			
			if (StringUtils.isBlank(channelSerJson) || channelSerJson.equals("null")) {
				String sql = SQLConf.getSql("ChannelSer", "findsercode");

				Map<String, Object> map = new HashMap<String, Object>();

				map.put("channelid", channelid);

				List<ChannelSer> list = channelSerDao.find(sql, map);

				if (list != null && !list.isEmpty() && list.size() > 0) {
					channelSer= list.get(0);
					
					RedisUtil.setStr(key, JSONObject.toJSONString(channelSer), Parameters.REDIS_ONE, 259200);		
				} else {
		
				}

			}else {
				channelSer=JSONObject.parseObject(channelSerJson,ChannelSer.class);	
			}
		} catch (Exception e) {
		}finally{
			return channelSer;
	} 
	}

}
