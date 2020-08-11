package com.xdream.wisdom.service.database.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.xdream.kernel.sql.SQLConf;
import com.xdream.wisdom.config.Parameters;
import com.xdream.wisdom.dao.ICustDao;
import com.xdream.wisdom.entity.Cust;
import com.xdream.wisdom.service.database.ICustDBService;
import com.xdream.wisdom.util.RedisUtil;
@Service("custDBService")
public class CustDBService implements ICustDBService {

	@Resource(name = "custDao")
	private ICustDao custDao;
	/**
	 * 查询用户所有的校验信息
	 */
	public Cust findCustcCheckInfo(String custid,String paltproductid){
		Cust cust=null;
		String custJson=null;
		
		String  key=custid+paltproductid+"_cust_normal";
		
		try {
			custJson= RedisUtil.getStrVal(key, Parameters.REDIS_ONE);
			
			if (StringUtils.isBlank(custJson) || custJson.equals("null")) {
				String sql = SQLConf.getSql("custInfo", "findByCustIdAndproduct");

				Map<String, Object> map = new HashMap<String, Object>();

				map.put("custid", custid);
				map.put("platproductid", paltproductid);

				List<Cust> list = custDao.find(sql, map);

				if (list != null && !list.isEmpty() && list.size() > 0) {
					cust= list.get(0);
					
					RedisUtil.setStr(key, JSONObject.toJSONString(cust), Parameters.REDIS_ONE, 259200);		
				} else {
		
				}

			}else {
				cust=JSONObject.parseObject(custJson,Cust.class);	
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			return cust;
	} 
}

}