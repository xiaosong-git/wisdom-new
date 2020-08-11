package com.xdream.wisdom.service.biz.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.xdream.wisdom.util.encryption.MD5Util;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xdream.wisdom.config.Parameters;
import com.xdream.wisdom.entity.Cust;
import com.xdream.wisdom.entity.ReqTxCodeDep;
import com.xdream.wisdom.entity.TransLs;
import com.xdream.wisdom.service.biz.ICustomerService;
import com.xdream.wisdom.service.database.ICustDBService;
import com.xdream.wisdom.service.database.IRequestTxDBService;
import com.xdream.wisdom.service.database.ITranslsDBService;
import com.xdream.wisdom.util.encryption.SecurityUtil;
import com.xdream.wisdom.util.response.ResultData;
@Service("customerService")
public class CustomerService implements ICustomerService {
	private static  Logger logger= LoggerFactory.getLogger(CustomerService.class);
	
	
	@Resource(name = "custDBService")
	private ICustDBService custDBService;
	
	@Resource(name = "requestTxDBService")
	private IRequestTxDBService requestTxDBService;
	
	@Resource(name = "translsDBService")
	private ITranslsDBService translsDBService;

	private static final String[] PRODUCTTYPES={"0012","0002","0003","0009","0202","0203"};
	/**报文头校验
	 * 返回参数定义0-成功 1-请求报文头信息不全 2-商户不存在 3-客户状态失效
	 * 4-商户请求业务编码不存在或商户为开通该业务 5-商户账户不可用-商户已停止改业务 6-商户账户余额不足 7-商户mac校验失败，
	 */
	@Override
	public String checkReqMsg(Map<String,String> custHeader,Map<String,String> custBody) {
		String custId =custHeader.get("custid");//客户号
		//String txcode=custHeader.get("txcode");//交易码
		String productcode=custHeader.get("productcode");//平台产品编号
		String serialno=custHeader.get("serialno");//流水号
		String mac=custHeader.get("mac");//商户签名
		logger.debug("请求报文体头信息为："+custId+"|"+productcode+"|"+serialno+"|"+mac);
		//基础校验
		if(StringUtils.isBlank(custId)||StringUtils.isBlank(productcode)||StringUtils.isBlank(serialno)){
			return  ResultData.resultMap(Parameters.ERR_FLAG, "00001", "请求报文头信息不全", serialno) ;
		}
		//商户不存在
		Cust cust =custDBService.findCustcCheckInfo(custId,productcode);
		/**
		 * 根据客户和产品查询所有基础信息和账户信息
		 * SELECT
		 * 	tc.custid,
		 * 	tc.custstate,
		 * 	tc.appid,
		 * 	tc.appkey,
		 * 	ta.custacct,
		 * 	ta.acctstate,
		 * 	ta.payflag,
		 * 	ta.balance,
		 * 	tp.platproductid,
		 * 	tp.STATUS,
		 * 	tp.productPrice
		 * FROM
		 * 	tbl_cust tc,
		 * 	tbl_cust_acct ta,
		 * 	tbl_plat_product tp
		 * WHERE
		 * 	tc.custid = ta.custid
		 * 	AND ta.custacct = CONCAT( tc.custid, tp.platproductid )
		 * 	AND tc.custid ='1000000001'
		 * 	AND tp.platproductid ='000006'		结果集
		 */
		if(null ==cust){
			return  ResultData.resultMap(Parameters.ERR_FLAG, "00002", "请求商户不存在或商户未开通该业务", serialno) ;
		}
		//商户状态异常
		String custState =cust.getCuststate();
		if(!StringUtils.isBlank(custState)&&"1".equals(custState)){
			return  ResultData.resultMap(Parameters.ERR_FLAG, "00003", "商户状态异常", serialno) ;
		}
		//业务编号校验
		String paltproductcodeString =cust.getPlatproductid();
		if(null==paltproductcodeString||"".equals(paltproductcodeString)){
			return  ResultData.resultMap(Parameters.ERR_FLAG, "00004", "业务编号不存在", serialno) ;
		}
		//平台产品已下线
		String platproductStatusString =cust.getStatus();
		if(null!=platproductStatusString&&"1".equals(paltproductcodeString)){
			return  ResultData.resultMap(Parameters.ERR_FLAG, "00004", "该产品已下线，请联系管理员", serialno) ;
		}
		String custAcct =cust.getCustacct();
		if(null==custAcct||"".equals(custAcct)){
			return  ResultData.resultMap(Parameters.ERR_FLAG, "00004", "商户未开通该业务", serialno) ;
		}
		//账户状态
		String custAcctState =cust.getAcctstate();
		if(!"0".equals(custAcctState)){
			return  ResultData.resultMap(Parameters.ERR_FLAG, "00005", "账户状态异常", serialno) ;
		}
		//商户余额 先支付在使用的需判断商户余额
		String payFlag =cust.getPayflag();
		BigDecimal balance =cust.getBalance();
		BigDecimal productPrice =cust.getProductPrice();
		if("1".equals(payFlag)&&(balance.compareTo(productPrice)==-1||balance.compareTo(BigDecimal.ZERO) == 0)){
			return  ResultData.resultMap(Parameters.ERR_FLAG, "00006", "账户余额不足", serialno) ;
		}
		// 验证流水号
		/**
		 * SELECT
		 * 	serialno,
		 * 	custid,
		 * 	custacct,
		 * 	transdate,
		 * 	amt,
		 * 	productcode,
		 * 	succ_flag,
		 * 	remark,
		 * 	requestdata,
		 * 	responsedata,
		 * 	responsetime
		 * FROM
		 * 	tbl_transls
				* WHERE
				* 	serialno ='156889411185'
				*/
		TransLs transls = translsDBService.findBySerialno(serialno);
		if(transls!=null){
			return ResultData.resultMap(Parameters.ERR_FLAG, "00004","流水号重复", serialno);
		}
		// 验证签名  商户号+订单号+时间+产品编码+秘钥
		 String signSource = custId  + productcode+serialno+cust.getAppkey();
		 boolean isSign = false;
		 try {
		 		isSign = SecurityUtil.verify(Arrays.asList(PRODUCTTYPES).contains(productcode)? MD5Util.MD5Encode(signSource,"UTF-8"):mac, signSource);
			 } catch (Exception e) {
				 e.printStackTrace();
				 return  ResultData.resultMap(Parameters.ERR_FLAG, "zzzzz", "系统异常，请联系管理员", serialno) ;
			 }
			 if (!isSign) {			
				 return  ResultData.resultMap(Parameters.ERR_FLAG, "00007", "mac校验失败", serialno) ;
			 }
			 //报文体校验
		String bodychenck =checkMsgBody(productcode,serialno,custBody); //校验上游请求报文
		JSONObject bodyresult=JSON.parseObject(bodychenck);
		if(!"0".equals(bodyresult.get("succ_flag"))){
			return bodychenck;
		}
		return  ResultData.resultMap(Parameters.SUCC_FLAG, "00000", "交易成功", serialno) ;
	}
	
	/**
	 * 请求报文体校验
	 * @param //tx_code
	 * @param custBody
	 * @return
	 */
	public String checkMsgBody(String productcode,String serialno,Map custBody){
		List<ReqTxCodeDep> reqList =requestTxDBService.findReqTxDep(productcode); //查询请求报文报文体
		//判断请求交易码是否存在
		if(null==reqList||reqList.size()==0){
			return  ResultData.resultMap(Parameters.ERR_FLAG, "00008", "平台产品无对应交易", serialno) ;//请求交易码不存在
		}else {
			for(int t =0 ;t<reqList.size();t++){
				ReqTxCodeDep reqTxBean =reqList.get(t);
				String msgName =reqTxBean.getMsgname();//详细接口字段  接口字段名

				String isrequire = reqTxBean.getIsrequire();//是否必输 0:必输：1非必输
				//判断是否上送字段
				if(!custBody.containsKey(msgName) && "0".equals(isrequire)){
					return  ResultData.resultMap(Parameters.ERR_FLAG, "00009", "报文"+msgName+"缺失", serialno) ;
				}else{
					if("0".equals(isrequire)){
						String key_value =(String)custBody.get(msgName);
						//判断上送字段是否有值
						//判断isrequire 是否是0 必输
						if("".equals(key_value)){
							return  ResultData.resultMap(Parameters.ERR_FLAG, "00010", "报文"+msgName+"值不能为空", serialno) ;
						}else{
							//判断字段是否超长
							String length =reqTxBean.getMsglength();
							logger.debug("请求报文"+key_value+"报文规定长度为:"+length);
							logger.debug("请求报文"+key_value+"报文实际长度为:"+key_value.length());
							if(key_value.length()>Integer.valueOf(length)){
								return  ResultData.resultMap(Parameters.ERR_FLAG, "00011", "报文"+msgName+"长度超长，规定长度"+length+",实际长度为："+key_value.length(), serialno) ;//字段超长
							}
						}
					}

				}
				
			}
		}
		return  ResultData.resultMap(Parameters.SUCC_FLAG, "00000", "交易成功", serialno) ;
	}

}
