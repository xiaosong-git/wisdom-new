package com.xdream.wisdom.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.xdream.kernel.dao.jdbc.Table;
import com.xdream.kernel.entity.Entity;
@Table(name="tbl_transls")
public class TransLs extends Entity{
	
	private String  serialno;//流水号
	private String  custid;//客户号
	private String custacct;//客户账号
	private Date transdate;//交易日期
	private BigDecimal amt;//交易金额
	private String productcode;//产品编号
	private String succ_flag;//交易结果
	private String remark;//备注
	private String requestdata;//请求数据包
	private String responsedata;//反馈数据包
	private String responsetime;//反馈时间

	public String getSerialno() {
		return serialno;
	}
	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}
	public String getCustid() {
		return custid;
	}
	public void setCustid(String custid) {
		this.custid = custid;
	}
	public String getCustacct() {
		return custacct;
	}
	public void setCustacct(String custacct) {
		this.custacct = custacct;
	}
	public BigDecimal getAmt() {
		return amt;
	}
	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}
	public String getProductcode() {
		return productcode;
	}
	public void setProductcode(String string) {
		this.productcode = string;
	}
	public String getSucc_flag() {
		return succ_flag;
	}
	public void setSucc_flag(String succ_flag) {
		this.succ_flag = succ_flag;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getRequestdata() {
		return requestdata;
	}
	public void setRequestdata(String requestdata) {
		this.requestdata = requestdata;
	}
	public String getResponsedata() {
		return responsedata;
	}
	public void setResponsedata(String responsedata) {
		this.responsedata = responsedata;
	}
	public Date getTransdate() {
		return transdate;
	}
	public void setTransdate(Date transdate) {
		this.transdate = transdate;
	}
	public String getResponsetime() {
		return responsetime;
	}
	public void setResponsetime(String responsetime) {
		this.responsetime = responsetime;
	}
	

}
