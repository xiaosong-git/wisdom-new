package com.xdream.wisdom.config;
/***
 * 全局参数配置
 * @author Administrator
 *
 */
public class Parameters {
	
	//Redis库
	public final static int REDIS_ONE=1;
	public final static int REDIS_TWO=2;
	public final static int REDIS_THREE=3;
	
	//返回参数
	public final static String SUCC_FLAG="0";
	public final static String ERR_FLAG="1";
	
	
	//上游报文类型
	public final static String JSON_TYPE="application/json";//纯json
	public final static String FORM_TYPE="application/x-www-form-urlencoded";//表单
	public final static String TEXT_TYPE="text/plain";//文本
	public final static String XML_TYPE ="application/xml";//xml
	public final static String  MULT_TYPE="multipart/form-data";//复合型表单，包含文本
	
	
	public final static String ACTION_INSERT="insert";
	public final static String ACTION_UPDATE="update";

	public final static String platProductCode_two="0002";//二要素 返回不带照片的
	public final static String platProductCode_three="0003";//三要素 请求参数带照片，返回参数不带照片
	public final static String platProductCode_two_photo="0009";//二要素 返回带照片的
	
	public final static String QUEUE_STRING="gol.queue";


	public final static String SUCCESS="0000";
	public final static String UNKNOW="unknow";
	public final static String FAIL="fail";

	//返回信息
	public final static String messgae_0="认证结果不一致";
	public final static String messgae_1="认证结果一致";
	public final static String messgae_2="查无此号";
	public final static String messgae_3="系统异常，请联系管理员";
	public final static String messgae_4="请求参数有误";
	public final static String messgae_5="认证失败，请重新发起认证";
	public final static String messgae_hhtp_fail="请求连接失败,请联系管理员";
	
}
