package com.xdream.wisdom.service.biz;

import java.util.Map;


/**
 * 公共商户校验接口
 * @author Administrator
 *
 */
public interface ICustomerService {
	
	public String checkReqMsg(Map<String,String> custHeader,Map<String,String> custBody)throws Exception;;

}
