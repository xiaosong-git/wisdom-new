package com.xdream.wisdom.entity;

import com.xdream.kernel.dao.jdbc.Table;
import com.xdream.kernel.entity.Entity;

@Table(name="tbl_req_tx_code")
public class ReqTxCode extends Entity{

	private String txcode;	//交易码
	private String msgtype; //报文类型1-json 暂定
	
	
	public String getTxcode() {
		return txcode;
	}
	public void setTxcode(String txcode) {
		this.txcode = txcode;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	
	
}
