package com.xdream.wisdom.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.xdream.kernel.dao.jdbc.Table;
import com.xdream.kernel.entity.Entity;

/*
 * 商户表
 */
@SuppressWarnings("serial")
@Table(name="tbl_cust")
public class Cust extends Entity{

	private String custid;       	//客户编号冲1000000000开始自增
	private String custname;		//客户名称
	private String certno;			//营业执照号
	private String legalperson;		//法人代表
	private String officephone;		//联系电话
	private String address;			//联系地址
	private String custstate;		//客户状态（0-正常，1-无效)
	private Date updatedate;		//更新时间
	private String appid;			//客户私钥
	private String appkey;			//客户公钥
	
	
	//扩展字段 账户信息
	private String custacct;	//客户账户，一个客户可对应多个账户，每个账户对应一个订单客户编号+产品编码
	private BigDecimal balance;	//账户余额(可为负数，支付方式为2时根据交易流水更新余额)
	private String acctstate;	//账户状态（0-正常，1-无效）
	private String payflag;		//支付方式1-先充值在使用 2-先使用后付款
	
	private String platproductid;		//平台产品编号-规则：XS+2位类型+4位编码(0001开始)
	private BigDecimal productPrice;	//产品单价
	private String status;
	
	public String getCustid() {
		return custid;
	}
	public void setCustid(String custid) {
		this.custid = custid;
	}
	public String getCustname() {
		return custname;
	}
	public void setCustname(String custname) {
		this.custname = custname;
	}
	public String getCertno() {
		return certno;
	}
	public void setCertno(String certno) {
		this.certno = certno;
	}
	public String getLegalperson() {
		return legalperson;
	}
	public void setLegalperson(String legalperson) {
		this.legalperson = legalperson;
	}
	public String getOfficephone() {
		return officephone;
	}
	public void setOfficephone(String officephone) {
		this.officephone = officephone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCuststate() {
		return custstate;
	}
	public void setCuststate(String custstate) {
		this.custstate = custstate;
	}
	public Date getUpdatedate() {
		return updatedate;
	}
	public void setUpdatedate(Date updatedate) {
		this.updatedate = updatedate;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getAppkey() {
		return appkey;
	}
	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}
	public String getCustacct() {
		return custacct;
	}
	public void setCustacct(String custacct) {
		this.custacct = custacct;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public String getAcctstate() {
		return acctstate;
	}
	public void setAcctstate(String acctstate) {
		this.acctstate = acctstate;
	}
	public String getPayflag() {
		return payflag;
	}
	public void setPayflag(String payflag) {
		this.payflag = payflag;
	}
	public String getPlatproductid() {
		return platproductid;
	}
	public void setPlatproductid(String platproductid) {
		this.platproductid = platproductid;
	}
	public BigDecimal getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(BigDecimal productPrice) {
		this.productPrice = productPrice;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
