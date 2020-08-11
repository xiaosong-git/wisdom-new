package com.xdream.wisdom.service.database.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xdream.kernel.sql.SQLConf;
import com.xdream.wisdom.config.Parameters;
import com.xdream.wisdom.dao.IReqTxCodeDepDao;
import com.xdream.wisdom.entity.ReqTxCodeDep;
import com.xdream.wisdom.service.database.IRequestTxDBService;
import com.xdream.wisdom.util.RedisUtil;
@Service("requestTxDBService")
public class RequestTxDBService implements IRequestTxDBService{

	@Resource(name = "reqTxCodeDepDao")
	private IReqTxCodeDepDao reqTxCodeDepDao;
	@Override
	public List<ReqTxCodeDep> findReqTxDep(String productcode) {
		String reqTxCodeDepJson=null;
		
		List<ReqTxCodeDep> list=null;
		String  key=productcode+"_txcode_normal";
		
		try {
			reqTxCodeDepJson= RedisUtil.getStrVal(key, Parameters.REDIS_ONE);
			
			if (StringUtils.isBlank(reqTxCodeDepJson) || reqTxCodeDepJson.equals("null")) {
				String sql = SQLConf.getSql("requestTxcodeDep", "searchTxDep");

				Map<String, Object> map = new HashMap<String, Object>();

				map.put("productcode", productcode);

				list = reqTxCodeDepDao.find(sql, map);

				if (list != null && !list.isEmpty() && list.size() > 0) {
					
					RedisUtil.setStr(key, JSONObject.toJSONString(list), Parameters.REDIS_ONE, 259200);		
				} else {
		
				}

			}else {
				list=JSONArray.parseArray(reqTxCodeDepJson,ReqTxCodeDep.class);	
			}
		} catch (Exception e) {
		}finally{
			return list;
	} 
	}

}
