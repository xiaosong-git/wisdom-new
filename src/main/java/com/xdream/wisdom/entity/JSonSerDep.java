package com.xdream.wisdom.entity;

import com.xdream.kernel.dao.jdbc.Table;
import com.xdream.kernel.entity.Entity;

/**
 * 通道报文类型为JSON请求包
 * @author wgm create by 2019-04-09
 *
 */
@Table(name="tbl_json_msg_dep")
public class JSonSerDep extends Entity{
	
	private String servercode;//服务码
	private String impmsg;//报文接口字段
	private String msglength;//报文长度
	private String nullable;//是否必输
	private String ismac;//是否签名字段
	private String defvalue;//默认值
	public String getServercode() {
		return servercode;
	}
	public void setServercode(String servercode) {
		this.servercode = servercode;
	}
	
	public String getImpmsg() {
		return impmsg;
	}
	public void setImpmsg(String impmsg) {
		this.impmsg = impmsg;
	}
	public String getMsglength() {
		return msglength;
	}
	public void setMsglength(String msglength) {
		this.msglength = msglength;
	}
	public String getNullable() {
		return nullable;
	}
	public void setNullable(String nullable) {
		this.nullable = nullable;
	}
	public String getIsmac() {
		return ismac;
	}
	public void setIsmac(String ismac) {
		this.ismac = ismac;
	}
	public String getDefvalue() {
		return defvalue;
	}
	public void setDefvalue(String defvalue) {
		this.defvalue = defvalue;
	}

}
