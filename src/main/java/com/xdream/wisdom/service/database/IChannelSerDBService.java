package com.xdream.wisdom.service.database;

import com.xdream.wisdom.entity.ChannelSer;

public interface IChannelSerDBService {
	
	public ChannelSer findSerTxInfo(String channelid,String custid) throws Exception;

}
