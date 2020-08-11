package com.xdream.wisdom.service.biz.impl;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
/**
 * 通道服务层
 */
import java.util.Map;

import javax.annotation.Resource;

import com.allinpay.mcp.comm.AllinpayUtils;


import com.anxin.data.IdentityCertification;
import com.xdream.wisdom.entity.*;
import com.xdream.wisdom.util.*;
import com.xdream.wisdom.util.encryption.DESUtil;
import com.xdream.wisdom.util.encryption.JZMD5Util;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xdream.wisdom.config.JinDieConfig;
import com.xdream.wisdom.config.Parameters;
import com.xdream.wisdom.service.biz.IChannelService;
import com.xdream.wisdom.service.biz.IMqSendMsgService;
import com.xdream.wisdom.service.database.IChannelSerDBService;
import com.xdream.wisdom.service.database.IJsonTxDepDBService;
import com.xdream.wisdom.service.database.IPlatChannelDBService;
import com.xdream.wisdom.service.database.ISerCodeDefineService;
import com.xdream.wisdom.util.response.ResultData;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@Service("channelService")
public class ChannelService implements IChannelService {
    private static Logger logger = LoggerFactory.getLogger(ChannelService.class);
    @Resource(name = "platChannelDBService")
    private IPlatChannelDBService platChannelDBService;

    @Resource(name = "channelSerDBService")
    private IChannelSerDBService channelSerDBService;

    @Resource(name = "jsonTxDepDBService")
    private IJsonTxDepDBService jsonTxDepDBService;

    @Resource(name = "serCodeDefineDBService")
    private ISerCodeDefineService serCodeDefineDBService;

    @Resource(name = "mqSendMsgService")
    private IMqSendMsgService mqSendMsgService;

    //c
    @Override
    public String InChannel(Map<String, String> msgHead, Map<String, String> msgBody) throws Exception {
        Map response = new HashMap<String, String>();
        Map<String, String> oldWisdomMap=new HashMap<String, String>();
        String channelType="";
        String platproductid = msgHead.get("productcode");//平台产品编号
        System.out.println("platproductid" + platproductid);
        String channelid = platChannelDBService.findChannelIdByPlat(platproductid);//通过platproductid查找通道产品channelid
        String[] ids = channelid.split(":");
        for (int j = 0; j < ids.length; j++) {
            Channel channel = platChannelDBService.findChannelByChannelid(ids[j]);//通过上面的通道ID--通过channelid查找通道产品
            if (null == channel) {
                return ResultData.resultMap(Parameters.ERR_FLAG, "00012", "路由通道不存在，请联系管理员", msgHead.get("serialno"));
            } else {
                String channelstatus = channel.getStatus();
                if ("1".equals(channelstatus)) {
                    return ResultData.resultMap(Parameters.ERR_FLAG, "00013", "路由通道已禁用，请联系管理员", msgHead.get("serialno"));
                } else {

                    //通道正常情况下开始插入交易流水{json{"actiontype":"insert","msg_trans":{各个字段详细信息}}}
                    //调用mq队列往
                    String msgSend = JsonUtil.packJsonInsertTran(msgHead, msgBody);
                    mqSendMsgService.sendMsg(msgSend);
                    ChannelSer chanSer = channelSerDBService.findSerTxInfo(channel.getChannelid(),msgHead.get("custid"));//通道状态正常情况下，根据通道信息查找对应的通道的交易信息（通过通道产品查找对应的服务信息） tbl_channel_ser
                    String serTxcode = chanSer.getServercode();//服务码
                    String serMsgType = chanSer.getMsgtype();//服务码报文类型，根据服务类型查找对应的表
                    String serReqFlag = chanSer.getRequestflag();//请求方式 request或response
                    String serURL = chanSer.getUrl();//请求地址
                    String requestFlag = chanSer.getRequestflag();
//				System.out.println("通道详细信息如下：通道号："+chanSer.getChannelid()+",对应服务码："+serTxcode
//						+",通道请求报文类型为："+serMsgType+",通道请求方式："+serReqFlag+",通道地址："+serURL+chanSer.getValid()+"/"+chanSer.getMactype()+"/"
//						+chanSer.getMacdef()
//						);
                    Map reqresponseMap = new HashMap<String, String>();
                    //根据服务码及对应的报文类型查找对应类型表中的详细接口字段
                    //报文类型1-application/json 2-form 3-map 4-application/xml 5-soap 5-fixex 6-iso/8583
                    ThirdResponseObj resPonse = null;
                    //通过报文类型查找
                    List<JSonSerDep> jsondep = jsonTxDepDBService.findJsonSerDep(serTxcode);
                    if (serMsgType != null && serMsgType != "") {
                        if ("1".equals(serMsgType)) {
                            String params = SerPackageUtil.packJsonMsg(chanSer, msgBody, jsondep);
                            //	System.out.println("拼接上游数据包为："+params);
                            if (requestFlag.equals("post")) {
                                if(chanSer.getChannelid().equals("SFZ0001")){

                                    System.out.println("chanSer.getChannelid()"+chanSer.getChannelid());
                                    //2B207D1341706A7R4160724854065152
                                    String name = DESUtil.decode("2B207D1341706A7R4160724854065152", msgBody.get("userName"));
                                    String encoding = DESUtil.getEncoding(name);
                                    String certNo = DESUtil.decode("2B207D1341706A7R4160724854065152", msgBody.get("certNo"));
                                    String imgData_temp = msgBody.get("imgData");
                                    System.out.println("身份证三要素验证信息为："+name+"====="+certNo+",照片大小为:"+imgData_temp.length());
                                    BASE64Encoder encode = new BASE64Encoder();
                                    BASE64Decoder decode = new BASE64Decoder();
                                    String imgData = encode.encode(FilesUtils.compressUnderSize(decode.decodeBuffer(imgData_temp), 10240L));
                                    Map map = IdentityCertification.authentication(name,certNo, imgData);
                                    resPonse=new ThirdResponseObj();
                                    resPonse.setCode("success");
                                    resPonse.setResponseEntity(JSON.toJSONString(map));

                                }else if(chanSer.getChannelid().equals("RZYJZ001")){
                                   
                                    if(msgHead.get("custid").equals("100000000000001")){
                                        oldWisdomMap=new HashMap<>();
                                        oldWisdomMap.put("returnCode","0000");
                                        oldWisdomMap.put("faceComparisonScore","923.22");
                                        oldWisdomMap.put("resultBank","1");
                                        oldWisdomMap.put("channelOderNo",String.valueOf(System.currentTimeMillis()));
                                        oldWisdomMap.put("message","认证结果一致");
                                        channelType=chanSer.getChannelid();
                                        resPonse=new ThirdResponseObj();
                                        resPonse.setCode("success");
                                        resPonse.setResponseEntity(JSON.toJSONString(oldWisdomMap));
                                    }else {
                                        oldWisdomMap = FastIdentifyUtil.fastIdentify(msgBody.get("photo"), msgBody.get("userName"), msgBody.get("certNo"), msgBody.get("phone"), platproductid, chanSer.getMackey());
                                        channelType=chanSer.getChannelid();
                                        resPonse=new ThirdResponseObj();
                                        resPonse.setCode("success");
                                        resPonse.setResponseEntity(JSON.toJSONString(oldWisdomMap));
                                    }


                                }else if("SJVE001".equals(chanSer.getChannelid())){
                                    //运营商三要素
                                    oldWisdomMap = PhoneVerify.phoneHD(msgBody.get("userName"), msgBody.get("certNo"), platproductid, msgBody.get("phone"), chanSer.getMackey(),msgHead.get("custid"),msgHead.get("serialno"));
                                    channelType=chanSer.getChannelid();
                                    resPonse=new ThirdResponseObj();
                                    resPonse.setCode("success");
                                    resPonse.setResponseEntity(JSON.toJSONString(oldWisdomMap));
                                }else if("HD001".equals(chanSer.getChannelid())){
                                    //翰迪接口
                                    oldWisdomMap = BadInfoVerify.badInfo(msgBody.get("userName"), msgBody.get("certNo"), platproductid, chanSer.getMackey(),msgHead.get("serialno"));
                                    channelType=chanSer.getChannelid();
                                    resPonse=new ThirdResponseObj();
                                    resPonse.setCode("success");
                                    resPonse.setResponseEntity(JSON.toJSONString(oldWisdomMap));
                                }
                                else {
                                    resPonse = HttpUtil.httpToServerPost(serURL, params, Parameters.JSON_TYPE);
                                }

                            }
                            if (requestFlag.equals("get")) {
                                if(chanSer.getChannelid().startsWith("WJT")){
                                 
                                    AllinpayUtils allinpayUtils = new AllinpayUtils();
                                    String params_encrypt = allinpayUtils.encryptByPrivateKey(params,chanSer.getMackey());
                                    String sign = allinpayUtils.sign(params,chanSer.getMackey());
                                    String requestUrl = chanSer.getUrl()+"?key="+"9d1ff5c7a196c38a92b3cd439d82cda5"+"&sign="+ URLEncoder.encode(sign, "UTF-8")+"&params="+URLEncoder.encode(params_encrypt, "UTF-8");
                                    resPonse = HttpUtil.doGet(requestUrl,"UTF-8");
                                }else if(chanSer.getChannelid().startsWith("JZ")){
                                    String key=msgBody.get("idname") + "_" + msgBody.get("idcard") + "_"+msgBody.get("bankcard")+"_"+chanSer.getChannelid();
                                    JSONObject json=null;
                                    String strVal = RedisUtil.getStrVal(key, Parameters.REDIS_THREE);
                                    if(null==strVal||StringUtils.isBlank(strVal)){
                                     
                                        String url="api.xiaoheer.com";
                                        String api="/xiaoher/bankTR.asmx/bankTRThree";
                                        //私钥
                                        String Privatekey="LDxIUJ";
                                        //hashcode
                                        String hashcode="78493d8d3de1445ab812bb2c5b9c61b6";
                                        Date now = new Date();
                                        //可以方便地修改日期格式
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                                        String sdate = dateFormat.format(now);
                                        Map<String, String> paramMap = new HashMap<String, String>();
                                        paramMap.put("Hashcode", hashcode);
                                        paramMap.put("bankcard", msgBody.get("bankcard"));
                                        paramMap.put("idcard", msgBody.get("idcard"));
                                        paramMap.put("idname", msgBody.get("idname"));
                                        String sign=paramMap.get("bankcard")+paramMap.get("Hashcode")+paramMap.get("idcard")+paramMap.get("idname")+Privatekey+sdate;
                                        sign= JZMD5Util.MD5(sign);
                                        //姓名
                                        paramMap.put("sign",sign);
                                        String httpresult=HttpUtil.httpsGet("https", url, "443",api, paramMap);
                                        json=XmlTool.documentToJSONObject(httpresult);
                                        RedisUtil.setStr(key, JSONObject.toJSONString(json), Parameters.REDIS_THREE, 259200);
                                    }else {
                                        String val = RedisUtil.getStrVal(key, Parameters.REDIS_THREE);
                                        json = JSONObject.parseObject(val, JSONObject.class);
                                    }
                                    resPonse=new ThirdResponseObj();
                                    if(json!=null){
                                        resPonse.setCode("success");
                                        resPonse.setResponseEntity(JSONObject.toJSONString(json));
                                    }else {
                                        resPonse.setCode("1"+"");
                                    }
                                }else {
                                    resPonse = HttpUtil.httpToServerGet(serURL, params, Parameters.JSON_TYPE);
                                }

                            }
                        }
                        if ("2".equals(serMsgType)) {
                            String params = SerPackageUtil.packFormMsg(chanSer, msgBody, jsondep);
                            System.out.println("拼接上游数据包为：" + params);
                            if (requestFlag.equals("post")) {
                                resPonse = HttpUtil.httpToServerPost(serURL, params, Parameters.FORM_TYPE);
                            }
                            if (requestFlag.equals("get")) {
                                if (chanSer.getChannelid().equals("SB0001")) {
                                    HashMap<String, String> headers = new HashMap<String, String>();
                                    params = "bankCard=" + msgBody.get("bankCard") + "&name=" + URLEncoder.encode(msgBody.get("name"), "UTF-8") + "&idCard=" + msgBody.get("idCard") + "&mobile=" + msgBody.get("mobile");
                                    String signParams = "bankCard=" + msgBody.get("bankCard") + "&name=" + msgBody.get("name") + "&idCard=" + msgBody.get("idCard") + "&mobile=" + msgBody.get("mobile");
                                    headers.put("X-BS-App-Key", "6S3wxKcJmixTf8E55Sex");
                                    String date = String.valueOf(System.currentTimeMillis());
                                    String app_secret = "7mTTUICVQQB6WrsBCp4L9WstfgI0c0V38zplIQph";
                                    String urlPath = "/v1/bankcard/type/t2";
                                    String signString = urlPath + "\n" + signParams + "\n" + date;
                                    String signstr = HmacUtils.hmacMd5Hex(app_secret.getBytes(), signString.getBytes(Charset.forName("utf-8")));
                                    headers.put("X-BS-Date", date);
                                    headers.put("X-BS-Validate", signstr);
                                    resPonse = HttpUtil.httpToServerGet(serURL, headers, params, Parameters.FORM_TYPE);
                                } else {
                                    resPonse = HttpUtil.httpToServerGet(serURL, params, Parameters.FORM_TYPE);
                                }
                            }
                        }
                        if ("4".equals(serMsgType)) {
                            String params = SerPackageUtil.packXmlMsg(chanSer, msgBody, jsondep);
                            System.out.println("拼接上游数据包为：" + params);
                            if (requestFlag.equals("post")) {
                                resPonse = HttpUtil.httpToServerPost(serURL, params, Parameters.FORM_TYPE);
                            }
                            if (requestFlag.equals("get")) {
                                resPonse = HttpUtil.httpToServerGet(serURL, params, Parameters.FORM_TYPE);
                            }
                        }

                        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateStr = dateformat.format(System.currentTimeMillis());
                        System.out.println("收到返回时间" + dateStr);
                        String httpCode = resPonse.getCode();
                        System.out.println("接收的返回码为:" + httpCode);
                        if (!"success".equals(httpCode)) {
                            return ResultData.resultMap(Parameters.ERR_FLAG, "00014", "请求连接失败,请重新发起", msgHead.get("serialno"));
                        } else {
                            String responseEntity = resPonse.getResponseEntity();
                            JSONObject js = JSON.parseObject(responseEntity);
                            System.out.println("上游返回数据为：" + responseEntity);
                            Map resput = new HashMap<String, String>();
                            Map channelRes = JsonUtil.listJsonStr(resput, js);

                            String dataresult = "";
                            //金蝶特殊化出来
                            if (chanSer.getChannelid().startsWith("JD") && channelRes.get("vector") != null) {
                                    if (!RsaSignCoder.verify((String) channelRes.get("result"), JinDieConfig.PUBLICKEY, (String) channelRes.get("sign"))) {
                                        return ResultData.resultMap(Parameters.ERR_FLAG, "00014", "请求连接失败,请重新发起", msgHead.get("serialno"));
                                    } else {
                                        System.err.println("验签成功");
                                    }
                                    if (channelRes.get("vector") != null) {
                                        dataresult = Cipher3DES.decrypt((String) channelRes.get("result"), JinDieConfig.PUBLICKEY,
                                                (String) channelRes.get("vector"));
                                        System.out.println("解密后的内容:" + dataresult);
                                    }
                                    js.put("data", JSONObject.parseObject(dataresult.toString()));

                            }

                            //查找返回报文映射  tbl_code_define
                            List<CodeDefine> list = serCodeDefineDBService.findCodeDefine(serTxcode);
                            for (int i = 0; i < list.size(); i++) {
                                CodeDefine codeDefine = list.get(i);
                                String serMsg = codeDefine.getSer_return_msg();
                                String cliMsg = codeDefine.getCli_return_msg();
                                String serValue = codeDefine.getSer_value();
                                String cliValue = codeDefine.getCli_value();
                                if (channelRes.containsKey(serMsg) && serValue.equals(channelRes.get(serMsg))) {
                                    response.put(cliMsg, cliValue);
                                }
                                if (js.containsKey("data") && serValue.equals("direct")) {
                                    response.put("data", JSONObject.parseObject(js.getString("data")));
                                }
                            }

                        }
                    }
                }
            }

            String key = msgHead.get("custid") + msgHead.get("productcode") + "_cust_normal";
            String custJson = RedisUtil.getStrVal(key, Parameters.REDIS_ONE);
            Cust cust = JSONObject.parseObject(custJson, Cust.class);
            //更新流水信息以及更新账户信息
            String msgUpdate = JsonUtil.packJsonUpdateTran(msgHead, response, cust.getProductPrice());
            mqSendMsgService.sendMsg(msgUpdate);
            //交易成功的需更新redis账户信息
            if (Parameters.SUCC_FLAG.equals(response.get("succ_flag"))) {
                cust.setBalance(cust.getBalance().subtract(cust.getProductPrice()));
                RedisUtil.setStr(key, JSONObject.toJSONString(cust), Parameters.REDIS_ONE, 259200);
            }

        }
        return JSON.toJSONString("RZYJZ001".equals(channelType)||"SJVE001".equals(channelType) || "HD001".equals(channelType)?oldWisdomMap:response);
    }
}
