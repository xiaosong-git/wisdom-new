package com.xdream.wisdom.entity;

import com.xdream.kernel.dao.jdbc.Table;
import com.xdream.kernel.entity.Entity;

@Table(name="tbl_req_tx_code_dep")
public class ReqTxCodeDep extends Entity{

	
	private String txcode;	//交易码
	private String msgname;	//详细接口字段
	private String msglength;	//字段长度
	private String isrequire;//是否必输 0:必输：1非必输
	
	public String getTxcode() {
		return txcode;
	}
	public void setTxcode(String txcode) {
		this.txcode = txcode;
	}
	public String getMsgname() {
		return msgname;
	}
	public void setMsgname(String msgname) {
		this.msgname = msgname;
	}
	public String getMsglength() {
		return msglength;
	}
	public void setMsglength(String msglength) {
		this.msglength = msglength;
	}


	public String getIsrequire() {
		return isrequire;
	}

	public void setIsrequire(String isrequire) {
		this.isrequire = isrequire;
	}
}
