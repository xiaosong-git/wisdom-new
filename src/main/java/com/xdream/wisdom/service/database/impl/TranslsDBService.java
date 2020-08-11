package com.xdream.wisdom.service.database.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xdream.kernel.sql.SQLConf;
import com.xdream.wisdom.dao.ITranslsDao;
import com.xdream.wisdom.entity.Channel;
import com.xdream.wisdom.entity.TransLs;
import com.xdream.wisdom.service.database.ITranslsDBService;
import com.xdream.wisdom.util.DateUtil;

@Service("translsDBService")
public class TranslsDBService implements ITranslsDBService {
	@Resource(name = "translsDao")
	private ITranslsDao translsDao;

	@Override
	public Long insertTransls(String serialno, String custid, String custacct, Date transdate, BigDecimal amt,
			String string, String succ_flag, String remark, String requestdata, String responsedata,
			String responsetime) throws Exception {
		String sql = SQLConf.getSql("transls", "insert");
		TransLs transls = new TransLs();
		transls.setSerialno(serialno);
		transls.setCustid(custid);
		transls.setCustacct(custacct);
		transls.setTransdate(transdate);
		transls.setProductcode(string);
		transls.setAmt(amt);
		transls.setSucc_flag(succ_flag);
		transls.setRemark(remark);
		transls.setRequestdata(requestdata.length()>2000?requestdata.substring(0,2000):requestdata);
		transls.setResponsedata(responsedata);
		transls.setResponsetime(responsetime);
		return translsDao.insert(sql, transls);
	}

	@Override
	public int modifyResult(String serialno, String custid, String responsedata, String succ_flag, String responsetime,
			BigDecimal amt) throws Exception {
		String sql = SQLConf.getSql("transls", "modifyResult");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("amt", amt);
		map.put("serialno", serialno);
		map.put("custid", custid);
		map.put("responsedata", responsedata);
		map.put("succ_flag", succ_flag);
		map.put("responsetime", responsetime);
		int i = translsDao.update(sql, map);
		return i;
	}

	@Override
	public TransLs findBySerialno(String serialno) {
		TransLs transls;
		String sql = SQLConf.getSql("transls","findBySerialno");
		Map<String,Object> map =new HashMap<String,Object>();
		map.put("serialno",serialno);
		List<TransLs> list = translsDao.find(sql,map);
		if(list != null && !list.isEmpty() && list.size() > 0) {
			transls = list.get(0);
		}else {
			transls = null;
		}
		return transls;
	}

}
