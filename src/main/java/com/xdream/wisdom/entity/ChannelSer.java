package com.xdream.wisdom.entity;

import com.xdream.kernel.dao.jdbc.Table;
import com.xdream.kernel.entity.Entity;

/*
 * 通道服务表
 */

@Table(name="tbl_channel_ser")
public class ChannelSer extends Entity{

	private String servercode;	//服务码
	private String channelid;	//上游产品编号
	private String msgtype;		//详细接口字段
	private String url;			//请求通道
	private String requestflag;	//请求方式（post or get）
	private String valid;  //是否签名
	private String mackey;//秘钥
	private String macdef;//签名字段 多个字段用":"分隔
	private String mactype;//加密方式  MD5 3DES RSA BASE64 暂定4种
	private String childsercode;//子交易码
	
	
	public String getChildsercode() {
		return childsercode;
	}
	public void setChildsercode(String childsercode) {
		this.childsercode = childsercode;
	}
	public String getValid() {
		return valid;
	}
	public void setValid(String valid) {
		this.valid = valid;
	}
	public String getMackey() {
		return mackey;
	}
	public void setMackey(String mackey) {
		this.mackey = mackey;
	}

	public String getMacdef() {
		return macdef;
	}
	public void setMacdef(String macdef) {
		this.macdef = macdef;
	}
	public String getMactype() {
		return mactype;
	}
	public void setMactype(String mactype) {
		this.mactype = mactype;
	}
	public String getServercode() {
		return servercode;
	}
	public void setServercode(String servercode) {
		this.servercode = servercode;
	}
	public String getChannelid() {
		return channelid;
	}
	public void setChannelid(String channelid) {
		this.channelid = channelid;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getRequestflag() {
		return requestflag;
	}
	public void setRequestflag(String requestflag) {
		this.requestflag = requestflag;
	}
	
	
	
}
