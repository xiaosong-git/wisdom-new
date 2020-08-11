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
import com.xdream.wisdom.dao.ICodeDefineDao;
import com.xdream.wisdom.dao.IReqTxCodeDepDao;
import com.xdream.wisdom.entity.CodeDefine;
import com.xdream.wisdom.entity.ReqTxCodeDep;
import com.xdream.wisdom.service.database.ISerCodeDefineService;
import com.xdream.wisdom.util.RedisUtil;
@Service("serCodeDefineDBService")
public class SerCodeDefineDBService implements ISerCodeDefineService {

	@Resource(name = "codeDefineDao")
	private ICodeDefineDao codeDefineDao;
	@Override
	public List<CodeDefine> findCodeDefine(String sercode) throws Exception {
		String CodeDefineJson=null;
		
		List<CodeDefine> list=null;
		String  key=sercode+"_codedefine_normal";
		
		try {
			CodeDefineJson= RedisUtil.getStrVal(key, Parameters.REDIS_ONE);
			
			if (StringUtils.isBlank(CodeDefineJson) || CodeDefineJson.equals("null")) {
				String sql = SQLConf.getSql("sercodedefine", "findeSerRes");

				Map<String, Object> map = new HashMap<String, Object>();

				map.put("servercode", sercode);

				list = codeDefineDao.find(sql, map);

				if (list != null && !list.isEmpty() && list.size() > 0) {
					
					RedisUtil.setStr(key, JSONObject.toJSONString(list), Parameters.REDIS_ONE, 259200);		
				} else {
		
				}

			}else {
				list=JSONArray.parseArray(CodeDefineJson,CodeDefine.class);	
			}
		} catch (Exception e) {
		}finally{
			return list;
	} 

}
}