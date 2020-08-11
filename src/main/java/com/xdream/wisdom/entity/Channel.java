package com.xdream.wisdom.entity;

import java.math.BigDecimal;

import com.xdream.kernel.dao.jdbc.Table;
import com.xdream.kernel.entity.Entity;

/*
 * 上游通道表
 */
@Table(name="tbl_channel")
public class Channel extends Entity{

	private String channelid;		//上游产品编号
	private String channelname;		//上游产品名称
	private BigDecimal cost;		//上游产品成本
	private String status;			//上游通道状态
	
	
	public String getChannelid() {
		return channelid;
	}
	public void setChannelid(String channelid) {
		this.channelid = channelid;
	}
	public String getChannelname() {
		return channelname;
	}
	public void setChannelname(String channelname) {
		this.channelname = channelname;
	}
	public BigDecimal getCost() {
		return cost;
	}
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
