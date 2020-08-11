package com.xdream.wisdom.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xdream.wisdom.config.JiTuiConfig;
import com.xdream.wisdom.config.Parameters;
import com.xdream.wisdom.util.encryption.MD5Util;
import com.xdream.wisdom.util.encryption.SecurityUtil;
import com.xdream.wisdom.util.response.ResultData;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class PhoneVerify {

    private static Logger logger= LoggerFactory.getLogger(PhoneVerify.class);

    public static Map<String, String> phoneHD(String userName,String certNo,String productCode,String phone,String macKey,String custid,String serialno) throws Exception {

        try {
            System.out.println("开始进入验证");
            if ("0202".equals(productCode)) {
                return	phoneVerity(userName, certNo, productCode,phone,macKey,custid,serialno);//简版
            }else if ("0203".equals(productCode)) {
                return  phoneVerity(userName, certNo, productCode,phone,macKey,custid,serialno);//详版
            }else {
                return	ResultData.phoneResult(Parameters.FAIL, "产品编码有误！", productCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("请求异常 "+productCode+":",e);
            return	ResultData.phoneResult(Parameters.FAIL, Parameters.messgae_3, productCode);
        }
    }




    public static Map<String, String> phoneVerity(String userName,String certNo,String platProductCode,
                                           String phone,String macKey,String custid,String serialno) throws Exception {

        Long startTime=System.currentTimeMillis();
        System.out.println("实际验证");
        try {
            if (StringUtils.isNotBlank(userName)) {
                userName = SecurityUtil.decodeString(userName,macKey);
            }
            if (StringUtils.isNotBlank(certNo)) {
                certNo = SecurityUtil.decodeString(certNo, macKey);
            }
            if (StringUtils.isNotBlank(phone)) {
                phone = SecurityUtil.decodeString(phone, macKey);
            }

        } catch (Exception e) {
            return	ResultData.phoneResult(Parameters.FAIL, "加密字段解析错误", serialno);
        }

        Map<String,String> channelResult=new HashMap<>();
        if("0202".equals(platProductCode)){
            String key=userName + "_" + phone + "_"+certNo+"_"+platProductCode;
            String strVal = RedisUtil.getStrVal(key, Parameters.REDIS_THREE);
            if(null==strVal||StringUtils.isBlank(strVal)){
                //没有数据 查询接口并放到redis中
                channelResult=phoneVerify(serialno, userName, phone, certNo, custid);
                RedisUtil.setStr(key, JSONObject.toJSONString(channelResult), Parameters.REDIS_THREE, 259200);
            }else {
                //有数据直接从redis中获取 返回
                String val = RedisUtil.getStrVal(key, Parameters.REDIS_THREE);
                channelResult = JSONObject.parseObject(val, Map.class);
                System.out.println("r d d phoneHD01");
            }
        }else if("0203".equals(platProductCode)) {
            String key=userName + "_" + phone + "_"+certNo+"_"+platProductCode;
            String strVal = RedisUtil.getStrVal(key, Parameters.REDIS_THREE);
            if(null==strVal||StringUtils.isBlank(strVal)){
                //没有数据 查询接口并放到redis中
                channelResult=phoneDetailVerify(serialno, userName, phone, certNo, custid);
                RedisUtil.setStr(key, JSONObject.toJSONString(channelResult), Parameters.REDIS_THREE, 259200);
            }else {
                //有数据直接从redis中获取 返回
                String val = RedisUtil.getStrVal(key, Parameters.REDIS_THREE);
                channelResult = JSONObject.parseObject(val, Map.class);
                System.out.println("r d d phoneHD02");
            }
        }
        Long endTime=System.currentTimeMillis();
        System.err.println("调用通道完成认证时间："+endTime+"-"+startTime+"="+(endTime-startTime));
        return  channelResult;

    }



    public static Map<String, String> phoneVerify(String serialno, String userName, String phone, String certNo, String custid) throws Exception {

        // 地址
        String urlString = JiTuiConfig.urlPrefix + "/api/mobile/factor3";

        Long timestamp = System.currentTimeMillis();

        String sign = JiTuiConfig.appkey + JiTuiConfig.appsecret + timestamp;//md5(appkey+appsecret+timestamp)，32小写

        sign = MD5Util.MD5(sign,"UTF-8");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mobile", phone);
        jsonObject.put("idNo", certNo);
        jsonObject.put("name",userName);
        jsonObject.put("appkey", JiTuiConfig.appkey);
        jsonObject.put("timestamp", timestamp+"");
        jsonObject.put("sign", sign);
        String json_getToken = jsonObject.toJSONString();
        System.out.println("极推三要素查询请求参数:"+json_getToken+"=="+serialno+"=="+custid+"调用通道完成时间："+(System.currentTimeMillis()-timestamp));

        StringEntity entity =new StringEntity(json_getToken,"UTF-8");
        entity.setContentType("application/json");
        ThirdResponseObj obj =   HttpUtil.http2Se(urlString,entity,"UTF-8");
        String httpCode =obj.getCode();
        Map<String, String>resultMap=new HashMap<String, String>();

        if (!"success".equals(httpCode)){
            resultMap.put("returnCode", "fail");
            resultMap.put("message", "请求连接失败，请重新发起连接");
            resultMap.put("merchOrderId", serialno);
            return resultMap;
        }


        String resultStr=obj.getResponseEntity();
        System.out.println("极推三要素简版查询："+resultStr+"==返回码："+obj.getCode()+"==订单号："+serialno+"=="+custid);

        JSONObject json= JSONArray.parseObject(resultStr);

        String msg=json.get("msg").toString();

        String code=json.get("code").toString();


        if ("0".equals(code)){
            JSONObject result= (JSONObject) json.get("result");
            String isTrue=String.valueOf(result.get("identical"));
            if ("true".equals(isTrue)){
                resultMap.put("returnCode", "0000");
                resultMap.put("message", "验证成功");
                resultMap.put("merchOrderId", serialno);
                return resultMap;
            }else if ("false".equals(isTrue)){
                resultMap.put("returnCode", "1004");
                resultMap.put("message", "信息不匹配");
                resultMap.put("merchOrderId", serialno);
                return resultMap;
            }else {
                resultMap.put("returnCode", "fail");
                resultMap.put("message", "请求联系管理员"+"（"+code+")");
                resultMap.put("merchOrderId", serialno);
                return resultMap;
            }

        }else if ("100".equals(code)) {
            resultMap.put("returnCode", "1003");
            resultMap.put("message", "查询无记录");
            resultMap.put("merchOrderId", serialno);
            return resultMap;
        }
        else if("004".equals(code)) {
            resultMap.put("returnCode", "fail");
            resultMap.put("message", "请求参数不完整");
            resultMap.put("merchOrderId", serialno);
            return resultMap;
        }else {
            resultMap.put("returnCode", "fail");
            resultMap.put("message", "请求联系管理员"+"（"+code+")");
            resultMap.put("merchOrderId", serialno);
            return resultMap;
        }

    }


    public static Map<String, String> phoneDetailVerify(String serialno, String userName, String phone, String certNo, String custid) throws Exception {

        // 地址
        String urlString = JiTuiConfig.urlPrefix + "/api/mobile/factor3s";

        Long timestamp = System.currentTimeMillis() ;

        String sign = JiTuiConfig.appkey + JiTuiConfig.appsecret + timestamp;//md5(appkey+appsecret+timestamp)，32小写


        sign = MD5Util.MD5(sign,"UTF-8");


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mobile", phone);
        jsonObject.put("idNo", certNo);
        jsonObject.put("name",userName);
        jsonObject.put("appkey", JiTuiConfig.appkey);
        jsonObject.put("timestamp", timestamp+ "");
        jsonObject.put("sign", sign);
        String json_getToken = jsonObject.toJSONString();
       // System.out.println("极推三要素详版请求参数:"+json_getToken+"=="+serialno+"=="+custid);
        System.out.println("极推三要素查询请求参数:"+json_getToken+"=="+serialno+"=="+custid+"调用通道完成时间："+(System.currentTimeMillis()-timestamp));

        StringEntity entity =new StringEntity(json_getToken,"UTF-8");
        entity.setContentType("application/json");

        ThirdResponseObj obj =   HttpUtil.http2Se(urlString,entity,"UTF-8");

        String httpCode=obj.getCode();

        Map<String, String>resultMap=new HashMap<String, String>();
        if (!"success".equals(httpCode)) {
            resultMap.put("returnCode", "fail");
            resultMap.put("message", "请求连接失败，请重新发起连接");
            resultMap.put("merchOrderId", serialno);
            resultMap.put("result", "");
            return resultMap;
        }

        String resultStr=obj.getResponseEntity();

        System.out.println("极推三要素详版查询："+resultStr+"==返回码："+obj.getCode()+"==订单号："+serialno+"=="+custid+"调用通道完成时间："+(System.currentTimeMillis()-timestamp));

        JSONObject json= JSONArray.parseObject(resultStr);

        String msg=String.valueOf(json.get("msg"));

        String code=String.valueOf(json.get("code"));

        if ("0".equals(code)){
            JSONObject data= (JSONObject) json.get("result");
            String result=data.getString("data");
            resultMap.put("returnCode", "0000");
            resultMap.put("message", "成功");
            resultMap.put("merchOrderId", serialno);
            resultMap.put("result", result);

        }else if ("004".equals(code)){
            resultMap.put("returnCode", "fail");
            resultMap.put("message", "请求参数缺失");
            resultMap.put("merchOrderId", serialno);
            resultMap.put("result", "");
        }else if("503".equals(code)){
            resultMap.put("returnCode", "fail");
            resultMap.put("message", "查询失败");
            resultMap.put("merchOrderId", serialno);
            resultMap.put("result", "");
        }else if("100".equals(code)){
            resultMap.put("returnCode", "fail");
            resultMap.put("message", "无对应记录");
            resultMap.put("merchOrderId", serialno);
            resultMap.put("result", "");
        }else {
            resultMap.put("returnCode", "fail");
            resultMap.put("message", "请联系管理员"+"("+code+")");
            resultMap.put("merchOrderId", serialno);
            resultMap.put("result", "");
        }
        return resultMap;


    }

}
