package com.xdream.wisdom.service.database.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xdream.kernel.sql.SQLConf;
import com.xdream.wisdom.dao.impl.CustAcctDao;
import com.xdream.wisdom.service.database.ICustAcctDBService;
import com.xdream.wisdom.util.DateUtil;
@Service("custAcctDBService")
public class CustAcctDBService implements ICustAcctDBService {
	@Resource(name = "custAcctDao")
	private CustAcctDao custAcctDao;

	@Override
	public int modifyCustAcct(String custacct, BigDecimal amt) throws Exception {
		String sql = SQLConf.getSql("custacct", "modifCustAcct");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("custacct", custacct);
		map.put("amt", amt);
		map.put("updatedate",DateUtil.getCurDate());
		int i = custAcctDao.update(sql, map);
		return i;
	}

}
