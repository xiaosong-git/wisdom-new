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
import com.xdream.wisdom.dao.IJsonSerDepDao;
import com.xdream.wisdom.entity.JSonSerDep;
import com.xdream.wisdom.entity.ReqTxCodeDep;
import com.xdream.wisdom.service.database.IJsonTxDepDBService;
import com.xdream.wisdom.util.RedisUtil;
@Service("jsonTxDepDBService")
public class JsonTxDepDBService implements IJsonTxDepDBService{

	@Resource(name = "jsonSerDepDao")
	private IJsonSerDepDao jsonSerDepDao;
	@Override
	public List<JSonSerDep> findJsonSerDep(String sercode) throws Exception {
		String jsonSerdep=null;
		
		List<JSonSerDep> list=null;
		String  key=sercode+"_jsonSerdep_normal";
		
		try {
			jsonSerdep= RedisUtil.getStrVal(key, Parameters.REDIS_ONE);
			
			if (StringUtils.isBlank(jsonSerdep) || jsonSerdep.equals("null")) {
				String sql = SQLConf.getSql("JsonSerTxDep", "findJsonSerTxDep");

				Map<String, Object> map = new HashMap<String, Object>();

				map.put("servercode", sercode);

				list = jsonSerDepDao.find(sql, map);

				if (list != null && !list.isEmpty() && list.size() > 0) {
					
					RedisUtil.setStr(key, JSONObject.toJSONString(list), Parameters.REDIS_ONE, 259200);		
				} else {
		
				}

			}else {
				list=JSONArray.parseArray(jsonSerdep,JSonSerDep.class);	
			}
		} catch (Exception e) {
		}finally{
			return list;
	} 
	}

}
