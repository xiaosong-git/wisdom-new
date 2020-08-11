package com.xdream.wisdom.service.biz.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.xdream.wisdom.config.Parameters;
import com.xdream.wisdom.service.database.impl.CustAcctDBService;
import com.xdream.wisdom.service.database.impl.TranslsDBService;
import com.xdream.wisdom.util.DateUtil;

@Component
public class QueueReceiver implements MessageListener {

	@Resource(name = "translsDBService")
	private TranslsDBService translsDBService;
	@Resource(name = "custAcctDBService")
	private CustAcctDBService custAcctDBService;

	// 消息处理模式json{"actiontype":"insert","msg_trans":{各个字段详细信息}}
	// 消息处理模式json{"actiontype":"update","msg_trans":{"transls"},"msg_custacct":{"custacct"}}}
	@Override
	public void onMessage(Message message) {
		try {
			String text = ((TextMessage) message).getText();
			JSONObject jsonObject = JSONObject.parseObject(text);
			String type = jsonObject.getString("actiontype");
			if (null != type && !"".equals(type)) {
				if (Parameters.ACTION_INSERT.equals(type)) {
					String trans = jsonObject.getString("msg_trans");
					JSONObject trans_ls = JSONObject.parseObject(trans);
					translsDBService.insertTransls(trans_ls.getString("serialno"), trans_ls.getString("custid"),
							trans_ls.getString("custacct"), trans_ls.getDate("transdate"),
							trans_ls.getBigDecimal("amt"), trans_ls.getString("productcode"),
							trans_ls.getString("succ_flag"), trans_ls.getString("remark"),
							trans_ls.getString("requestdata"), trans_ls.getString("responsedata"),
							trans_ls.getString("responsetime"));
				} else if (Parameters.ACTION_UPDATE.equals(type)) {
					// 更新是要同步更新流水表和账户表
					String msg_transString = jsonObject.getString("msg_trans");
					String msg_custacct = jsonObject.getString("msg_custacct");
					JSONObject JonsTrans = JSONObject.parseObject(msg_transString);
					JSONObject JonsCustAcct = JSONObject.parseObject(msg_custacct);
					int i = translsDBService.modifyResult(JonsTrans.getString("serialno"),
							JonsTrans.getString("custid"), JonsTrans.getString("responsedata"),
							JonsTrans.getString("succ_flag"), JonsTrans.getString("responsetime"),JonsCustAcct.getBigDecimal("amt"));
					if (i > 0 && Parameters.SUCC_FLAG.equals(JonsTrans.getString("succ_flag"))) {
						custAcctDBService.modifyCustAcct(JonsCustAcct.getString("custacct"),
								JonsCustAcct.getBigDecimal("amt"));
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
