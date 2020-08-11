package com.xdream.wisdom.service.database;


import com.xdream.wisdom.entity.Channel;

public interface IPlatChannelDBService {
	public Channel findChannelByPlat(String platproductid)throws Exception;
	public String findChannelIdByPlat(String platproductid)throws Exception;
	public Channel findChannelByChannelid(String channelid)throws Exception;
}
