package com.xdream.wisdom.entity;

import java.math.BigDecimal;

import com.xdream.kernel.dao.jdbc.Table;
import com.xdream.kernel.entity.Entity;

/*
 * 平台产品表
 */

@Table(name="tbl_plat_product")
public class PlatProduct extends Entity{

	private String platproductid;		//平台产品编号-规则：XS+2位类型+4位编码(0001开始)
	private String productName;			//平台产品名称
	private BigDecimal productPrice;	//产品单价
	private String channelid;			//上游产品编号
	private String status;				//产品状态
	
	
	public String getPlatproductid() {
		return platproductid;
	}
	public void setPlatproductid(String platproductid) {
		this.platproductid = platproductid;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public BigDecimal getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(BigDecimal productPrice) {
		this.productPrice = productPrice;
	}
	public String getChannelid() {
		return channelid;
	}
	public void setChannelid(String channelid) {
		this.channelid = channelid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
