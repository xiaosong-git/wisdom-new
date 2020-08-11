package com.xdream.wisdom.util;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.xdream.wisdom.config.JingZongConfig;
import com.xdream.wisdom.config.Parameters;
import com.xdream.wisdom.config.RzyVerifyConfig;
import com.xdream.wisdom.util.encryption.Base64;
import com.xdream.wisdom.util.encryption.MD5Util;
import com.xdream.wisdom.util.encryption.SecurityUtil;
import com.xdream.wisdom.util.response.JzResPacket;
import com.xdream.wisdom.util.response.MapResultData;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.*;

public class FastIdentifyUtil {

    public static Map<String,String> fastIdentify(String photo,String userName,String certNo,String phone,String productCode,String macKey)throws Exception{
        try {
            if (Parameters.platProductCode_two.equals(productCode)) { //二要素 返回不带照片的| String requestUrl="http://47.99.1.34/wisdom/rzy/rzyTwoVerify";
                return	judgeRzyVerify(userName, certNo, photo,phone,macKey,productCode);
            }else if (Parameters.platProductCode_three.equals(productCode)) {//三要素 请求参数带照片，返回参数不带照片 | String requestUrl="http://47.99.1.34/wisdom/rzy/rzyThreeVerify";
                String key=SecurityUtil.decodeString(userName,macKey)+ "_" + SecurityUtil.decodeString(certNo, macKey) + "_"+"0003";
                String strVal = RedisUtil.getStrVal(key, Parameters.REDIS_THREE);
                if(null==strVal||StringUtils.isBlank(strVal)){
                    photo= Base64.encode(FilesUtils.compressUnderSize(Base64.decode(photo), 10240L));//将照片压缩
                }
                return  judgeRzyThreeVerify(userName, certNo, photo,phone,macKey);
            }else if ("0012".equals(productCode)) {//敬众二要素|String requestUrl="http://47.99.1.34/wisdom/jinZong/jzTwoVerify";
                return judgeRzyVerify(userName,certNo,photo,phone,macKey,productCode); //return judgeRzyVerify(userName,certNo,photo,phone,macKey,requestUrl);  |twoJZVerify(userName,certNo)
            }else if("0009".equals(productCode)){//敬众二要素有照片|返回照片
                return	judgeRzyVerify(userName, certNo, photo,phone,macKey,productCode);
            }
            else {
                return MapResultData.resultMap(Parameters.ERR_FLAG, "编码有误", "10011", "");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return MapResultData.resultMap("1", "系统异常，请联系管理员", "10011", "");
        }

    }


    /**
     * 二要素认证rzy,jinzong（返回结果不带照片）
     */
    public static Map<String, String> judgeRzyVerify( String userName,  String certNo,
                                             String photo,String phone,String macKey,String productCode) throws Exception {
        //下游调用接口的时间
        Long startTime=System.currentTimeMillis();
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
            return MapResultData.resultMap(Parameters.ERR_FLAG, "100013", "加密字段解析错误", "10011");

        }
                //通道调用返回结果
        //     Map<String,String>   channelResult=callTwoOrThreeVerify(userName, certNo,photo, phone,requestUrl);
                Map<String,String> channelResult=null;
                if("0012".equals(productCode)){//敬众二要素
                    //从redis中取值 有返回 没有查询并存放redis中
                    String key=userName + "_" + certNo + "_"+productCode;
                    String strVal = RedisUtil.getStrVal(key, Parameters.REDIS_THREE);
                    if(null==strVal||StringUtils.isBlank(strVal)){
                        channelResult=twoJZVerify(userName,certNo);
                        RedisUtil.setStr(key, JSONObject.toJSONString(channelResult), Parameters.REDIS_THREE, 259200);
                    }else {
                        String val = RedisUtil.getStrVal(key, Parameters.REDIS_THREE);
                        channelResult = JSONObject.parseObject(val, Map.class);
                        System.out.println("r d d rz01");
                    }

                }else if("0009".equals(productCode)){//敬众二要素参数没有照片返回有照片
                    String key=userName + "_" + certNo + "_"+productCode;
                    String strVal = RedisUtil.getStrVal(key, Parameters.REDIS_THREE);
                    if(null==strVal||StringUtils.isBlank(strVal)){
                        channelResult=twoJZVerifyPhoto(userName,certNo);
                        RedisUtil.setStr(key, JSONObject.toJSONString(channelResult), Parameters.REDIS_THREE, 259200);
                    }else {
                        String val = RedisUtil.getStrVal(key, Parameters.REDIS_THREE);
                        channelResult = JSONObject.parseObject(val, Map.class);
                        System.out.println("r d d rz02");
                    }
                }else {
                    String key=userName + "_" + certNo + "_"+"0002";
                    String strVal = RedisUtil.getStrVal(key, Parameters.REDIS_THREE);
                    if(null==strVal||StringUtils.isBlank(strVal)){
                        channelResult=twoRzyVerify(userName,certNo);
                        RedisUtil.setStr(key, JSONObject.toJSONString(channelResult), Parameters.REDIS_THREE, 259200);
                    }else {
                        String val = RedisUtil.getStrVal(key, Parameters.REDIS_THREE);
                        channelResult = JSONObject.parseObject(val, Map.class);
                        System.out.println("r d d rz03");
                    }
                }
                //结束时间
                Long endTime=System.currentTimeMillis();
                System.err.println("调用通道完成认证时间："+endTime+"-"+startTime+"="+(endTime-startTime));
                return  channelResult;
    }


    //敬众二要素返回照片
    public static Map<String, String> twoJZVerifyPhoto(String userName, String certNo)
            throws Exception {

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 可以方便地修改日期格式
        String sdate = dateFormat.format(now);

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("Hashcode", JingZongConfig.hashcode);

        paramMap.put("passName", userName);
        paramMap.put("pid", certNo);

        String sign = paramMap.get("Hashcode") + paramMap.get("passName")
                + paramMap.get("pid") + JingZongConfig.key + sdate;

        sign = MD5Util.MD5Encode2(sign, "UTF-8");

        paramMap.put("sign", sign);// 姓名


        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        System.out.println("敬众二要素返回带照片请求参数：" + paramMap + "==" + DateUtil.getSystemDateTime());

        Map<String, String> resultMap = new HashMap<String, String>();// 返回结果

        Long startTime = System.currentTimeMillis();
        // 开始请求，获取的是XML格式
        ThirdResponseObj obj = HttpUtil.http2Nvp(JingZongConfig.url2, nvps);

        Long endTime = System.currentTimeMillis();
        System.err.println("敬众二要素带照片通道使用时间：" + endTime + "-" + startTime + "=" + (endTime - startTime));

        String code = obj.getCode();
        String orderNo = OrderNoUtil.genOrderNo4Pre("ZY", 24);
        if ("success".equals(code)) {

            String resultStr = obj.getResponseEntity();

            System.out.println("敬众二要素带照片的原始返回参数：" + resultStr);

            JSONObject json = XmlTool.documentToJSONObject(resultStr);

            Gson gson = new Gson();
            //返回值
            JzResPacket resPacket = gson.fromJson(json.toString(),
                    JzResPacket.class);

            String error_code = resPacket.getErrorRes().get(0).getErr_code();
            String Error_content = resPacket.getErrorRes().get(0).getErr_content();
            String photo = resPacket.getPhoto();

            if ("200".equals(error_code)) {
                //验证结果一致
                resultMap.put("channelOderNo", orderNo);
                resultMap.put("returnCode", Parameters.SUCCESS);
                resultMap.put("message", Parameters.messgae_1);
                resultMap.put("resultBank", "1");
                resultMap.put("photo", photo);
            } else if ("401".equals(error_code)) {
                //验证结果不一至
                resultMap.put("channelOderNo", orderNo);
                resultMap.put("returnCode", Parameters.SUCCESS);
                resultMap.put("message", Parameters.messgae_0);
                resultMap.put("resultBank", "0");
                resultMap.put("photo", photo);

            } else if ("404".equals(error_code)) {
                //未匹配到身份信息
                resultMap.put("channelOderNo", orderNo);
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message", "未匹配到认证的身份信息");
                resultMap.put("resultBank", "4");
                resultMap.put("photo", "");
            } else if ("405".equals(error_code)) {
                //参数错误
                resultMap.put("channelOderNo", orderNo);
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message", Parameters.messgae_0);
                resultMap.put("resultBank", "0");
                resultMap.put("photo", "");
            }else if("503".equals(error_code)){
                resultMap.put("channelOderNo", orderNo);
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message", "此业务已关闭");
                resultMap.put("resultBank", "0");
                resultMap.put("photo", "");
            }
            else {
                resultMap.put("channelOderNo", orderNo);
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message", Parameters.messgae_0);
                resultMap.put("resultBank", "0");
            }

        } else {
            resultMap.put("channelOderNo", orderNo);
            resultMap.put("returnCode", Parameters.FAIL);
            resultMap.put("message", Parameters.messgae_hhtp_fail);
            resultMap.put("resultBank", "4");
            resultMap.put("photo", "");
        }
        return resultMap;

        //	return test1Photo();
    }



    /****三要素认证rzy(请求参数带照片,返回参数不带照片)**/
    public static Map<String, String> judgeRzyThreeVerify( String userName,  String certNo,
                                                  String photo,String phone,String macKey) throws Exception {
        //开始时间
        Long startTime=System.currentTimeMillis();
        System.out.println("******进入三要素认证判定接口（请求参数带照片,返回参数不带照片）******");


        byte[] frontIdCardPhoto1=null;
        byte []backIdCardPhoto1=null;
        byte []photo1=null;
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

            if (StringUtils.isNotBlank(photo)) {
                photo1=Base64.decode(photo);
            }

        } catch (Exception e) {
            return MapResultData.resultMap(Parameters.ERR_FLAG, "100013", "加密字段解析错误", "10011");
        }

        Map<String,String> resultMap=null;
        //从redis中取值 有返回 没有查询并存放redis中
        String key=userName + "_" + certNo + "_"+"0003";
        String strVal = RedisUtil.getStrVal(key, Parameters.REDIS_THREE);
        if(null==strVal||StringUtils.isBlank(strVal)){
            //没有数据 查询接口并放到redis中
            resultMap=threeRzyVerify(userName, certNo, frontIdCardPhoto1, backIdCardPhoto1, photo1);  /***调用通道**/
            RedisUtil.setStr(key, JSONObject.toJSONString(resultMap), Parameters.REDIS_THREE, 259200);
        }else {
            //有数据直接从redis中获取 返回
            String val = RedisUtil.getStrVal(key, Parameters.REDIS_THREE);
            resultMap = JSONObject.parseObject(val, Map.class);
            System.out.println("r d d rz04");
        }
       // Map<String,String>    channelResult=callTwoOrThreeVerify(userName, certNo,photo,  phone,requestUrl);
        Long endTime=System.currentTimeMillis();
        System.err.println("调用通道完成认证时间："+endTime+"-"+startTime+"="+(endTime-startTime));
        return resultMap;
    }





//    public static Map<String, String> callTwoOrThreeVerify(
//                                                    String userName, String certNo,
//                                                    String photo ,String phone,String requestUrl)throws Exception {
//        //最终返回结果
//        Map<String,String>resultMap=new HashMap<String,String>();
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("userName", userName );//加密
//        map.put("certNo", certNo );// 加密);
//        map.put("photo", photo);
//
//        ThirdResponseObj obj = HttpUtil.http2Nvp(requestUrl, map, "UTF-8");
//
//        String code = obj.getCode();// http返回码
//
//        if ("success".equals(code)) {// http连接成功连接成功
//            String json = obj.getResponseEntity();// 返回参数 为json字符串
//            resultMap=JSONObject.parseObject(json,Map.class);
//            return resultMap;
//        } else {
//            //resultMap.put("channelOderNo",OrderNoUtil.genOrderNo4Pre("ZY", 24));
//          //  resultMap.put("returnCode", Parameters.FAIL);
//            resultMap.put("message","认证连接异常,请联系管理人员");
//            resultMap.put("resultBank","4");
//            return resultMap;
//        }
//    }


//    public String getTwoVerified(String name, String certNo) throws Exception{
//        String key="TwoVerified"+name+certNo;
//        String str=null;
//        //设置存储在1 号库
//        str=	RedisUtil.getStrVal(key, Parameters.REDIS_TWO);
////		System.out.println("取得值："+str);
//        return str;
//    }




    public static Map<String, String> twoJZVerify(String userName, String certNo)
            throws Exception {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 可以方便地修改日期格式
        String sdate = dateFormat.format(now);

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("Hashcode", JingZongConfig.hashcode);

        paramMap.put("passName", userName);
        paramMap.put("pid", certNo);
        System.out.println("敬众二要素请求参数：" + paramMap);
        String sign = paramMap.get("Hashcode") + paramMap.get("passName")
                + paramMap.get("pid") + JingZongConfig.key + sdate;

        sign = MD5Util.MD5Encode2(sign, "UTF-8");

        paramMap.put("sign", sign);// 姓名

        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        Map<String, String> resultMap = new HashMap<String, String>();// 返回结果

        Long startTime = System.currentTimeMillis();
        // 开始请求，获取的是XML格式
        ThirdResponseObj obj = HttpUtil.http2Nvp(JingZongConfig.url1, nvps);

        Long endTime = System.currentTimeMillis();
        System.err.println("敬众二要素通道使用时间：" + endTime + "-" + startTime + "=" + (endTime - startTime));

        String code = obj.getCode();
        String orderNo = OrderNoUtil.genOrderNo("JZ", 24);
        if ("success".equals(code)) {
            String resultStr = obj.getResponseEntity();

            System.out.println("敬众二要素认证返回不带照片原始返回参数：" + resultStr);

            JSONObject json = XmlTool.documentToJSONObject(resultStr);

            Gson gson = new Gson();
            //返回值
            JzResPacket resPacket = gson.fromJson(json.toString(),
                    JzResPacket.class);

            String error_code = resPacket.getErrorRes().get(0).getErr_code();
            String Error_content = resPacket.getErrorRes().get(0).getErr_content();
            if ("200".equals(error_code)) {
                //验证结果一致
                resultMap.put("channelOderNo", orderNo);
                resultMap.put("returnCode", "0000");
                resultMap.put("message", Parameters.messgae_1);
                resultMap.put("resultBank", "1");
            } else if ("401".equals(error_code)) {
                //验证结果不一致
                resultMap.put("channelOderNo", orderNo);
                resultMap.put("returnCode", Parameters.SUCCESS);
                resultMap.put("message", Parameters.messgae_0);
                resultMap.put("resultBank", "0");

            } else if ("404".equals(error_code)) {
                //未匹配到身份信息
                resultMap.put("channelOderNo", orderNo);
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message", "未匹配到认证的身份信息");
                resultMap.put("resultBank", "2");
            } else if ("405".equals(error_code)) {
                //参数错误
                resultMap.put("channelOderNo", orderNo);
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message", Parameters.messgae_0);
                resultMap.put("resultBank", "4");
            } else {
                resultMap.put("channelOderNo", orderNo);
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message", Parameters.messgae_0);
                resultMap.put("resultBank", "4");
            }

        } else {
            resultMap.put("channelOderNo", orderNo);
            resultMap.put("returnCode", Parameters.FAIL);
            resultMap.put("message", Parameters.messgae_hhtp_fail);
            resultMap.put("resultBank", "3");
        }
        return resultMap;
    }




    public static Map<String, String> threeRzyVerify(String userName,
                                              String certNo, byte[] frontIdCardPhoto, byte[] backIdCardPhoto,
                                              byte[] photo) throws Exception {
        //	String appKey = RzyVerifyConfig.key;
        System.out.println("认证云三要素认证："+userName+"=="+certNo+"=="+DateUtil.getSystemDateTime());
        Map<String, String> parametersToSign = new TreeMap<String, String>();
        parametersToSign.put("appId", RzyVerifyConfig.merchantNo);
        parametersToSign.put("appKey", RzyVerifyConfig.key);
        parametersToSign.put("name", userName);
        parametersToSign.put("idNo", certNo);
        parametersToSign.put("mode", "8");
        String signature = null;

        try {
            //签名
            signature = SignatureUtilsRZY.buildSignature(parametersToSign, RzyVerifyConfig.key, SignatureType.MD5);
        } catch (Exception e) {
            // TODO: 处理签名失败的情况
            e.printStackTrace();
        }
        parametersToSign.put("signature", signature);
        Map<String,String>map=new HashMap<String,String>();
        map.put("appId", RzyVerifyConfig.merchantNo);
        map.put("appKey", RzyVerifyConfig.key);
        map.put("name", userName);
        map.put("idNo", certNo);
        map.put("mode", "8");
        map.put("signature", signature);
        Map<String,byte[]>photoMap=new HashMap<String,byte[]>();
        photoMap.put("photo", photo);

        if(frontIdCardPhoto!=null&&frontIdCardPhoto.length>0){
            photoMap.put("frontIdCardPhoto", frontIdCardPhoto);
        }
        if(backIdCardPhoto!=null&&backIdCardPhoto.length>0){
            photoMap.put("backIdCardPhoto", backIdCardPhoto);
        }
        Long startTime=System.currentTimeMillis();
        String respondentJsonString=HttpUtil.httpThree("http://www.idverify.cn/identityCloud/api/webApi/identification/fastIdentify", photoMap, "USELESS", parametersToSign);
        Long endTime=System.currentTimeMillis();
        System.err.println("认证云三要素通道使用时间："+endTime+"-"+startTime+"="+(endTime-startTime));
        //	String respondentJsonString=testString();

        return result(respondentJsonString);
    }


    public static   Map<String,String>result(String str)throws Exception{
        System.out.println("认证云通道返回的原始参数:"+str);
        String orderNo=OrderNoUtil.genOrderNo("ZY", 24);
        String facecomparisonscore ="0.0";//人脸比对评分
        if (StringUtils.isBlank(str)) {
            Map<String,String>resultMap=new HashMap<String,String>();
            resultMap.put("channelOderNo",orderNo);
            resultMap.put("returnCode", Parameters.FAIL);
            resultMap.put("message","认证失败，请重新发起认证");
            resultMap.put("resultBank","4");
            return resultMap;
        }

        Map<String,String>resultMap=new HashMap<String, String>();

        Map<String,String>strMap=new HashMap<String, String>();

        Map<String,String>detailMap=new HashMap<String, String>();

        strMap=JSONObject.parseObject(str,Map.class);



        String success=String.valueOf(strMap.get("success"));
        String code=String.valueOf(strMap.get("code"));

        String message=String.valueOf(strMap.get("message"));
        String data=String.valueOf(strMap.get("data"));

        String bankResult="";//返回信息描述
        String returnCode="";//返回码

        if (StringUtils.isBlank(success)&&StringUtils.isBlank(code)&&success==null&&code==null) {
            if (StringUtils.isBlank(message)||"null".equals(message)) {
                message=Parameters.messgae_5;
            }
            resultMap.put("channelOderNo",orderNo);
            resultMap.put("returnCode", Parameters.FAIL);
            resultMap.put("message",message );
            resultMap.put("resultBank","4");
            return resultMap;
        }else {
            String identica="";//通道验证结果


            if ("true".equals(success)&&"0".equals(code)) {

                Map<String,String>dataMap=new HashMap<String, String>();

                if (StringUtils.isBlank(data)||"null".equals(data)) {
                    if (StringUtils.isBlank(message)) {
                        message=Parameters.messgae_0;
                    }
                    resultMap.put("channelOderNo", orderNo);
                    resultMap.put("returnCode", Parameters.FAIL);
                    resultMap.put("message",message );
                    resultMap.put("resultBank","0");
                    resultMap.put("faceComparisonScore",facecomparisonscore);
                    return resultMap;
                }else {
                    dataMap=JSONObject.parseObject(data,Map.class);
                    identica=String.valueOf(dataMap.get("identical"));
                    orderNo=String.valueOf(dataMap.get("orderNo"));
                    String detail = String.valueOf(dataMap.get("detail"));
                    detailMap =JSONObject.parseObject(detail,Map.class);
                    //20190608update by-wgm
                    facecomparisonscore=String.valueOf(detailMap.get("faceComparisonScore"));
                    System.out.println("人脸比对结果评分："+facecomparisonscore);

                    if ("true".equals(identica)) {
                        bankResult="1";
                        message=Parameters.messgae_1;
                    }else {
                        bankResult="0";
                        message=Parameters.messgae_0;
                    }
                    resultMap.put("channelOderNo", orderNo);
                    resultMap.put("returnCode", Parameters.SUCCESS);
                    resultMap.put("message",message );
                    resultMap.put("resultBank",bankResult);
                    resultMap.put("faceComparisonScore",facecomparisonscore);
                    return resultMap;

                }


            }else if ("false".equals(success)) {

                bankResult="0";
                message=Parameters.messgae_0;
                resultMap.put("channelOderNo", orderNo);
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message",message );
                resultMap.put("resultBank",bankResult);
                return resultMap;
            }else {

                bankResult="0";
                message=Parameters.messgae_0;
                resultMap.put("channelOderNo", orderNo);
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message",message );
                resultMap.put("resultBank",bankResult);
                return resultMap;
            }

        }


    }


    public static Map<String, String> twoRzyVerify(String userName, String certNo
                                           ) throws Exception {
        //String appKey = RzyVerifyConfig.key;
        Map<String, String> parametersToSign = new TreeMap<String, String>();
        parametersToSign.put("appId", RzyVerifyConfig.merchantNo);
        parametersToSign.put("appKey", RzyVerifyConfig.key);
        parametersToSign.put("name", userName);
        parametersToSign.put("idNo", certNo);
        parametersToSign.put("mode", "7");

        String signature = null;
        try {
            //签名
            signature = SignatureUtilsRZY.buildSignature(parametersToSign, RzyVerifyConfig.key, SignatureType.MD5);
        } catch (Exception e) {
            // TODO: 处理签名失败的情况
            e.printStackTrace();
        }

        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("appId", RzyVerifyConfig.merchantNo));
        nvps.add(new BasicNameValuePair("appKey", RzyVerifyConfig.key));
        nvps.add(new BasicNameValuePair("signature", signature));
        nvps.add(new BasicNameValuePair("name", userName));
        nvps.add(new BasicNameValuePair("idNo", certNo));
        nvps.add(new BasicNameValuePair("mode", "7"));
        //测试
//		 return result(testString());
        Long startTime=System.currentTimeMillis();
        ThirdResponseObj obj	=	HttpUtil.http2Nvp(RzyVerifyConfig.url,nvps);
        Long endTime=System.currentTimeMillis();
        System.err.println("认证云二要素通道使用时间："+endTime+"-"+startTime+"="+(endTime-startTime));
        String code=obj.getCode();
        if (code.equals("success")) {
            String respondentJsonString=obj.getResponseEntity();

            return result(respondentJsonString);
        }else {
            Map<String,String>resultMap=new HashMap<String,String>();
            resultMap.put("channelOderNo", OrderNoUtil.genOrderNo("RZY", 24));
            resultMap.put("returnCode", Parameters.FAIL);
            resultMap.put("message",Parameters.messgae_hhtp_fail );
            resultMap.put("resultBank","4");
            return resultMap;

        }
    }

}
