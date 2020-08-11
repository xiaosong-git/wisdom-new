package com.xdream.wisdom.service.database;

import java.math.BigDecimal;
import java.util.Date;

import com.xdream.wisdom.entity.TransLs;

public interface ITranslsDBService {
	public Long insertTransls(String serialno, String custid, String custacct, Date transdate,
			BigDecimal amt, String productcode, String succ_flag, String remark, String requestdata, String responsedata,String responseTime)
			throws Exception;
	
	public int modifyResult(String serialno,String cust_id, String responsedata, String succ_flag,String responsetime,BigDecimal amt)throws Exception;
	
    public TransLs findBySerialno(String serialno);
}
