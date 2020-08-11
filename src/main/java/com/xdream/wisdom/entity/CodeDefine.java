package com.xdream.wisdom.entity;

import com.xdream.kernel.dao.jdbc.Table;
import com.xdream.kernel.entity.Entity;
@Table(name="tbl_code_define")
public class CodeDefine extends Entity{
	private String servercode;
	private String ser_return_msg;
	private String cli_return_msg;
	private String ser_value;
	private String cli_value;
	private String ext1;
	private String ext2;
	public String getServercode() {
		return servercode;
	}
	public void setServercode(String servercode) {
		this.servercode = servercode;
	}
	public String getSer_return_msg() {
		return ser_return_msg;
	}
	public void setSer_return_msg(String ser_return_msg) {
		this.ser_return_msg = ser_return_msg;
	}
	public String getCli_return_msg() {
		return cli_return_msg;
	}
	public void setCli_return_msg(String cli_return_msg) {
		this.cli_return_msg = cli_return_msg;
	}
	public String getSer_value() {
		return ser_value;
	}
	public void setSer_value(String ser_value) {
		this.ser_value = ser_value;
	}
	public String getCli_value() {
		return cli_value;
	}
	public void setCli_value(String cli_value) {
		this.cli_value = cli_value;
	}
	public String getExt1() {
		return ext1;
	}
	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}
	public String getExt2() {
		return ext2;
	}
	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}
	
}
