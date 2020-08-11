package com.xdream.wisdom.service.biz;

import javax.jms.Destination;

public interface IMqSendMsgService {
	
	public void sendMsg(String  json)throws Exception;

}
