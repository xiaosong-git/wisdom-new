package com.xdream.wisdom.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.xdream.kernel.dao.jdbc.Table;
import com.xdream.kernel.entity.Entity;

/*
 * 商户账户表
 */

@Table(name="tbl_cust_acct")
public class CustAcct extends Entity{

	private String custacct;	//客户账户，一个客户可对应多个账户，每个账户对应一个订单客户编号+产品编码
	private String custid;		//客户编号
	private BigDecimal balance;	//账户余额(可为负数，支付方式为2时根据交易流水更新余额)
	private String opendate;	//开户日期
	private String closedate;	//销户日期
	private String acctstate;	//账户状态（0-正常，1-无效）
	private String payflag;		//支付方式1-先充值在使用 2-先使用后付款
	private Date updatedate;	//更新时间
	
	
	
	public String getCustacct() {
		return custacct;
	}
	public void setCustacct(String custacct) {
		this.custacct = custacct;
	}
	public String getCustid() {
		return custid;
	}
	public void setCustid(String custid) {
		this.custid = custid;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public String getOpendate() {
		return opendate;
	}
	public void setOpendate(String opendate) {
		this.opendate = opendate;
	}
	public String getClosedate() {
		return closedate;
	}
	public void setClosedate(String closedate) {
		this.closedate = closedate;
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
	public Date getUpdatedate() {
		return updatedate;
	}
	public void setUpdatedate(Date updatedate) {
		this.updatedate = updatedate;
	}
	
	
}
