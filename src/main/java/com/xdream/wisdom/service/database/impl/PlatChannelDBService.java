package com.xdream.wisdom.service.database.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.xdream.kernel.sql.SQLConf;
import com.xdream.wisdom.config.Parameters;
import com.xdream.wisdom.dao.IChannelDao;
import com.xdream.wisdom.entity.Channel;
import com.xdream.wisdom.entity.Cust;
import com.xdream.wisdom.service.database.IPlatChannelDBService;
import com.xdream.wisdom.util.RedisUtil;
@Service("platChannelDBService")
public class PlatChannelDBService implements IPlatChannelDBService {

	@Resource(name = "channelDao")
	private IChannelDao channelDao;
	
	@Override
	public Channel findChannelByPlat(String platproductid) throws Exception {
		Channel Paltchannel=null;
		String channelJson=null;
		
		String  key=platproductid+"_channel_normal";
		
		try {
			channelJson= RedisUtil.getStrVal(key, Parameters.REDIS_ONE);
			
			if (StringUtils.isBlank(channelJson) || channelJson.equals("null")) {
				String sql = SQLConf.getSql("paltChannel", "findPlatChannel");

				Map<String, Object> map = new HashMap<String, Object>();

				map.put("platproductid", platproductid);

				List<Channel> list = channelDao.find(sql, map);

				if (list != null && !list.isEmpty() && list.size() > 0) {
					Paltchannel= list.get(0);
					
					RedisUtil.setStr(key, JSONObject.toJSONString(Paltchannel), Parameters.REDIS_ONE, 259200);		
				} else {
		
				}

			}else {
				Paltchannel=JSONObject.parseObject(channelJson,Channel.class);	
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			return Paltchannel;
	} 
	}
	@Override
	public String findChannelIdByPlat(String platproductid) throws Exception {
		// TODO Auto-generated method stub
		String channelId=null;
		String channelString =null;
		String  key=platproductid+"_channel_normal";
		try {
			channelString= RedisUtil.getStrVal(key, Parameters.REDIS_ONE);
			
			if (StringUtils.isBlank(channelString) || channelString.equals("null")) {
				String sql = SQLConf.getSql("paltChannel", "findPlatChannelId");

				Map<String, Object> map = new HashMap<String, Object>();

				map.put("platproductid", platproductid);

				List<Channel> list = channelDao.find(sql, map);

				if (list != null && !list.isEmpty() && list.size() > 0) {
					channelString= list.get(0).getChannelid();
					
					RedisUtil.setStr(key, channelString, Parameters.REDIS_ONE, 259200);		
				} else {
		
				}

			}else {
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			return channelString;
	    } 
	}
	@Override
	public Channel findChannelByChannelid(String channelid) throws Exception {
		// TODO Auto-generated method stub
		Channel Paltchannel=null;
		String channelJson=null;
		
		String  key=channelid+"_channel_normal";
		
		try {
			channelJson= RedisUtil.getStrVal(key, Parameters.REDIS_ONE);
			
			if (StringUtils.isBlank(channelJson) || channelJson.equals("null")) {
				String sql = SQLConf.getSql("paltChannel", "findPlatChannelByChannelid");

				Map<String, Object> map = new HashMap<String, Object>();

				map.put("channelid", channelid);

				List<Channel> list = channelDao.find(sql, map);

				if (list != null && !list.isEmpty() && list.size() > 0) {
					Paltchannel= list.get(0);
					
					RedisUtil.setStr(key, JSONObject.toJSONString(Paltchannel), Parameters.REDIS_ONE, 259200);		
				} else {
		
				}

			}else {
				Paltchannel=JSONObject.parseObject(channelJson,Channel.class);	
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			return Paltchannel;
	    } 
	}
}