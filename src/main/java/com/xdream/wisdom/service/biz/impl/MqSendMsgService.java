package com.xdream.wisdom.service.biz.impl;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.sun.istack.FinalArrayList;
import com.xdream.wisdom.service.biz.IMqSendMsgService;
@Service("mqSendMsgService")
public class MqSendMsgService implements IMqSendMsgService {
	
	@Resource(name="jmsQueueTemplate")
    private JmsTemplate jmsQueueTemplate;


	@Override
	public void sendMsg(final String json) throws Exception {
		//System.out.println("发送的消息为:"+json);
		//System.err.println("消息发送的地址:"+jmsQueueTemplate.getDefaultDestination());
		jmsQueueTemplate.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				if (session==null) {
					System.out.println("session为空");
				}else {
					System.out.println(session);
				}
				return session.createTextMessage(json);
			}
		});
		
	}

}
