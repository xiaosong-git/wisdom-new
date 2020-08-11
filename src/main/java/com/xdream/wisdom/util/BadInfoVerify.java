package com.xdream.wisdom.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xdream.wisdom.config.HandiConfig;
import com.xdream.wisdom.config.Parameters;
import com.xdream.wisdom.util.encryption.RSACoder;
import com.xdream.wisdom.util.encryption.SecurityUtil;
import com.xdream.wisdom.util.response.ResultData;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BadInfoVerify {

    public static Map<String, String> badInfo(String userName, String certNo, String platProductCode,
                                              String macKey, String serialno) throws Exception {

        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            if (StringUtils.isNotBlank(userName)) {
                userName = SecurityUtil.decodeString(userName, macKey);
            }
            if (StringUtils.isNotBlank(certNo)) {
                certNo = SecurityUtil.decodeString(certNo, macKey);
            }
        } catch (Exception e) {
            return ResultData.phoneResult(Parameters.FAIL, "加密字段解析错误", serialno);
        }

        try {
            if ("0201".equals(platProductCode)) {
                Map<String, Object> map = badInfoByHD(serialno, certNo, userName);
                resultMap = JSON.parseObject(JSON.toJSONString(map), Map.class);
                return resultMap;
            } else if ("W003".equals(platProductCode)) {
                Map<String, Object> map = developmentHD(serialno, certNo, userName);
                resultMap = JSON.parseObject(JSON.toJSONString(map), Map.class);
                return resultMap;
            } else if ("W004".equals(platProductCode)) {
                Map<String, Object> map = developmentPlusHD(serialno, certNo, userName);
                resultMap = JSON.parseObject(JSON.toJSONString(map), Map.class);
                return resultMap;
            }
//            else if("W001".equals(platProductCode)){
//                Map<String, Object> map=resultHandByHD()
//            }
            else {
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message", "没有可调用的产品编码");
                resultMap.put("merchOrderId", serialno);
                resultMap.put("resData", "");
                return resultMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("returnCode", Parameters.FAIL);
            resultMap.put("message", Parameters.messgae_3);
            resultMap.put("merchOrderId", serialno);
            resultMap.put("resData", "");
            return resultMap;
        }

    }


    public static Map<String, Object> badInfoByHD(String serialno, String certNo,
                                                  String userName) throws Exception {

        String mkey = UUID.randomUUID().toString();
        String xml = xmlString(serialno, certNo, userName);
        String strKey = RSACoder.encryptByPublicKey(new String(mkey.getBytes(),
                "utf-8"), HandiConfig.PUBLICKEY);

        String strxml = new String(Base64.encode(DesUtil.encrypt(xml.toString()
                .getBytes("utf-8"), mkey.getBytes())));

        String reqXml = new String(Base64.encode(HandiConfig.CHANNELID
                .getBytes("utf-8"))) + "|" + strKey + "|" + strxml;
        System.out.println("翰迪不良信息请求报文：" + xml);

        // 发送http请求并获取响应内容
        String reutrnResult = HttpUtil.sendXMLDataByPost(HandiConfig.URL,
                reqXml);

        String[] xmlArr = reutrnResult.split("\\|");

        Map<String, Object> resultMap = new HashMap<String, Object>();

        if (xmlArr[0].equals("0")) {// 失败

            String failMsg = new String(Base64.decode(xmlArr[2]), "utf-8");
            System.out.println("翰迪解密内容：" + failMsg + "==" + xmlArr[1] + "=="
                    + serialno);// BASE64(3DES(报文原文))

            String resCode = xmlArr[1];
            if ("9921".equals(resCode) || "9001".equals(resCode)) {
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message", failMsg + "(" + resCode + ")");
                resultMap.put("resData", "");
                return resultMap;
            }

            resultMap.put("returnCode", Parameters.FAIL);
            resultMap.put("message", "请求失败，请联系技术人员" + "(" + xmlArr[1] + ")");
            resultMap.put("resData", "");
        } else {
            byte[] b = DesUtil.decrypt(Base64.decode(xmlArr[1]),
                    mkey.getBytes());
            String tradeXml = new String(b, "utf-8");
            System.out.println("翰迪解密内容：" + tradeXml + "==" + xmlArr[0] + "=="
                    + serialno);// BASE64(3DES(报文原文))

            resultMap = JSONObject.parseObject(tradeXml, Map.class);

            String resCode = String.valueOf(resultMap.get("resCode"));
            String resMsg = String.valueOf(resultMap.get("resMsg"));
            String resData = String.valueOf(resultMap.get("resData"));

            if ("0000".equals(resCode)) {
                resultMap.put("returnCode", resCode);
                resultMap.put("message", "查询成功" + "(" + resCode + ")");
                resultMap.put("resData",
                        JSONObject.parseObject(resData, Map.class));
            } else if ("1001".equals(resCode)) {
                resultMap.put("returnCode", resCode);
                resultMap.put("message", "未查到不良信息或身份信息不一致未查到信息" + "(" + resCode
                        + ")");
                resultMap.put("resData", "");
            } else if ("9921".equals(resCode) || "9001".equals(resCode)) {
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message", resMsg + "(" + resCode + ")");
                resultMap.put("resData", "");
            } else {
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message", "请求失败，请联系技术人员" + "(" + resCode + ")");
                resultMap.put("resData", "");
            }

        }

        return resultMap;
    }


    public static String xmlString(String serialno, String certNo, String userName)
            throws Exception {
        String now = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<subatm>" + "<application>"
                + HandiConfig.APPLICATION
                + "</application>"
                + "<version>"
                + HandiConfig.VERSION
                + "</version>"
                + "<sendTime>"
                + DateUtil.getSystemTimeFourteen()
                + "</sendTime>"
                + "<transCode>"
                + HandiConfig.TRANSCODE
                + "</transCode>"
                + "<channelId>"
                + HandiConfig.CHANNELID
                + "</channelId>"
                + "<channelOrderId>"
                + serialno
                + "</channelOrderId>"
                + "<cid>"
                + certNo
                + "</cid>"
                + "<name>"
                + userName
                + "</name>"
                + "</subatm>";
        return xml;
    }


    public static Map<String, Object> developmentHD(String platOrderNo,
                                                    String certNo, String userName) throws Exception {

        String mkey = UUID.randomUUID().toString();
        //拼装报文
        String xml = xmlDevelopment(platOrderNo, certNo, userName);

        String strKey = RSACoder.encryptByPublicKey(new String(mkey.getBytes(),
                "utf-8"), HandiConfig.PUBLICKEY);

        String strxml = new String(Base64.encode(DesUtil.encrypt(xml.toString().getBytes("utf-8"), mkey.getBytes())));

        String reqXml = new String(Base64.encode(HandiConfig.CHANNELID.getBytes("utf-8"))) + "|" + strKey + "|" + strxml;

        System.out.println("重点人员拓展信息查询请求报文：" + xml);

        // 发送http请求并获取响应内容
        String reutrnResult = HttpUtil.sendXMLDataByPost(HandiConfig.URL, reqXml);

        Map<String, Object> resultMap = resultHandByHD(reutrnResult, platOrderNo, mkey);

        return resultMap;
    }

    /**
     * 重点人员拓展信息查询
     **/
    public static String xmlDevelopment(String orderNo, String certNo, String userName) throws Exception {
        String now = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<subatm>" + "<application>"
                + HandiConfig.APPLICATION
                + "</application>"
                + "<version>"
                + HandiConfig.VERSION
                + "</version>"
                + "<sendTime>"
                + DateUtil.getSystemTimeFourteen()
                + "</sendTime>"
                + "<transCode>"
                + HandiConfig.DEVELOPMENT_TRANSCODE
                + "</transCode>"
                + "<channelId>"
                + HandiConfig.CHANNELID
                + "</channelId>"
                + "<channelOrderId>"
                + orderNo
                + "</channelOrderId>"
                + "<cid>"
                + certNo
                + "</cid>"
                + "<name>"
                + userName
                + "</name>"
                + "</subatm>";

        return xml;
    }


    public static Map<String, Object> resultHandByHD(String reutrnResult, String platOrderNo, String mkey) throws Exception {
        String[] xmlArr = reutrnResult.split("\\|");

        Map<String, Object> resultMap = new HashMap<String, Object>();

        if (xmlArr[0].equals("0")) {// 失败

            String failMsg = new String(Base64.decode(xmlArr[2]), "utf-8");
            System.out.println("翰迪解密内容：" + failMsg + "==" + xmlArr[1] + "=="
                    + platOrderNo);// BASE64(3DES(报文原文))

            String resCode = xmlArr[1];
            if ("9921".equals(resCode) || "9001".equals(resCode)) {
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message", failMsg + "(" + resCode + ")");
                resultMap.put("resData", "");
                return resultMap;
            }

            resultMap.put("returnCode", Parameters.FAIL);
            resultMap.put("message", "请求失败，请联系技术人员" + "(" + xmlArr[1] + ")");
            resultMap.put("resData", "");
        } else {
            byte[] b = DesUtil.decrypt(Base64.decode(xmlArr[1]), mkey.getBytes());
            String tradeXml = new String(b, "utf-8");
            System.out.println("翰迪解密内容：" + tradeXml + "==" + xmlArr[0] + "=="
                    + platOrderNo);// BASE64(3DES(报文原文))

            resultMap = JSONObject.parseObject(tradeXml, Map.class);

            String resCode = String.valueOf(resultMap.get("resCode"));
            String resMsg = String.valueOf(resultMap.get("resMsg"));
            String resData = String.valueOf(resultMap.get("resData"));

            if ("0000".equals(resCode)) {
                resultMap.put("returnCode", resCode);
                resultMap.put("message", "查询成功" + "(" + resCode + ")");
                resultMap.put("resData",
                        JSONObject.parseObject(resData, Map.class));
            } else if ("1001".equals(resCode)) {
                resultMap.put("returnCode", resCode);
                resultMap.put("message", "未查到信息或身份信息不一致未查到信息" + "(" + resCode
                        + ")");
                resultMap.put("resData", "");
            } else if ("9921".equals(resCode) || "9001".equals(resCode)) {
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message", resMsg + "(" + resCode + ")");
                resultMap.put("resData", "");
            } else {
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message", "请求失败，请联系技术人员" + "(" + resCode + ")");
                resultMap.put("resData", "");
            }

        }

        return resultMap;
    }

    public static String xmldevelopmentPlus(String orderNo, String certNo, String userName) throws Exception {
        String now = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<subatm>" + "<application>"
                + HandiConfig.APPLICATION
                + "</application>"
                + "<version>"
                + HandiConfig.VERSION
                + "</version>"
                + "<sendTime>"
                + DateUtil.getSystemTimeFourteen()
                + "</sendTime>"
                + "<transCode>"
                + HandiConfig.DEVELOPMENTPLUS_TRANSCODE
                + "</transCode>"
                + "<channelId>"
                + HandiConfig.CHANNELID
                + "</channelId>"
                + "<channelOrderId>"
                + orderNo
                + "</channelOrderId>"
                + "<cid>"
                + certNo
                + "</cid>"
                + "<name>"
                + userName
                + "</name>"
                + "</subatm>";

        return xml;
    }

    /**
     * 重点人员核验信息
     */

    public static Map<String, Object> developmentPlusHD(String platOrderNo,
                                                        String certNo, String userName) throws Exception {

        String mkey = UUID.randomUUID().toString();
        //拼装报文
        String xml = xmldevelopmentPlus(platOrderNo, certNo, userName);

        String strKey = RSACoder.encryptByPublicKey(new String(mkey.getBytes(),
                "utf-8"), HandiConfig.PUBLICKEY);

        String strxml = new String(Base64.encode(DesUtil.encrypt(xml.toString().getBytes("utf-8"), mkey.getBytes())));

        String reqXml = new String(Base64.encode(HandiConfig.CHANNELID.getBytes("utf-8"))) + "|" + strKey + "|" + strxml;

        System.out.println("重点人员核验信息查询请求报文：" + xml);

        // 发送http请求并获取响应内容
        String reutrnResult = HttpUtil.sendXMLDataByPost(HandiConfig.URL, reqXml);
        System.out.println("原始返回报文：" + reutrnResult);
        String[] xmlArr = reutrnResult.split("\\|");

        Map<String, Object> resultMap = new HashMap<String, Object>();

        if (xmlArr[0].equals("0")) {
            // 失败
            String failMsg = new String(Base64.decode(xmlArr[2]), "utf-8");
            System.out.println("翰迪解密内容：" + failMsg + "==" + xmlArr[1] + "=="
                    + platOrderNo);// BASE64(3DES(报文原文))

            String resCode = xmlArr[1];
            if ("9921".equals(resCode) || "9001".equals(resCode)) {
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message", failMsg + "(" + resCode + ")");
                resultMap.put("resData", "");
                return resultMap;
            }

            resultMap.put("returnCode", Parameters.FAIL);
            resultMap.put("message", "请求失败，请联系技术人员" + "(" + xmlArr[1] + ")");
            resultMap.put("resData", "");
        } else {
            byte[] b = DesUtil.decrypt(Base64.decode(xmlArr[1]), mkey.getBytes());
            String tradeXml = new String(b, "utf-8");
            System.out.println("翰迪解密内容：" + tradeXml + "==" + xmlArr[0] + "=="
                    + platOrderNo);// BASE64(3DES(报文原文))

            resultMap = JSONObject.parseObject(tradeXml, Map.class);

            String resCode = String.valueOf(resultMap.get("resCode"));
            String resMsg = String.valueOf(resultMap.get("resMsg"));
            String resData = String.valueOf(resultMap.get("resData"));

            if ("0000".equals(resCode)) {
                resultMap.put("returnCode", resCode);
                resultMap.put("message", "查询成功" + "(" + resCode + ")");
                resultMap.put("resData", resData);
            } else if ("1001".equals(resCode)) {
                resultMap.put("returnCode", resCode);
                resultMap.put("message", "未查到信息或身份信息不一致未查到信息" + "(" + resCode
                        + ")");
                resultMap.put("resData", "");
            } else if ("9921".equals(resCode) || "9001".equals(resCode)) {
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message", resMsg + "(" + resCode + ")");
                resultMap.put("resData", "");
            } else {
                resultMap.put("returnCode", Parameters.FAIL);
                resultMap.put("message", "请求失败，请联系技术人员" + "(" + resCode + ")");
                resultMap.put("resData", "");
            }

        }

        return resultMap;
    }


    public static Map<String, Object> drivingHD(String platOrderNo,
                                                String certNo, String userName, String carNo) throws Exception {
        System.out.println("handiceshikaishi");
        String mkey = UUID.randomUUID().toString();

        String xml = xmlDriving(platOrderNo, certNo, userName, carNo);

        String strKey = RSACoder.encryptByPublicKey(new String(mkey.getBytes(),
                "utf-8"), HandiConfig.PUBLICKEY);

        String strxml = new String(Base64.encode(DesUtil.encrypt(xml.toString()
                .getBytes("utf-8"), mkey.getBytes())));

        String reqXml = new String(Base64.encode(HandiConfig.CHANNELID
                .getBytes("utf-8"))) + "|" + strKey + "|" + strxml;
        System.out.println("行驶证信息请求报文：" + xml);

        // 发送http请求并获取响应内容
        String reutrnResult = HttpUtil.sendXMLDataByPost(HandiConfig.URL,
                reqXml);
        Map<String, Object> resultMap = resultHandByHD(reutrnResult, platOrderNo, mkey);
        return resultMap;
    }

    /**
     * 行驶证查询
     **/
    public static String xmlDriving(String orderNo, String certNo, String userName,
                                    String carNo) throws Exception {
        String now = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<subatm>" + "<application>"
                + HandiConfig.APPLICATION
                + "</application>"
                + "<version>"
                + HandiConfig.VERSION
                + "</version>"
                + "<sendTime>"
                + DateUtil.getSystemTimeFourteen()
                + "</sendTime>"
                + "<transCode>"
                + HandiConfig.DRIVING_TRANSCODE
                + "</transCode>"
                + "<channelId>"
                + HandiConfig.CHANNELID
                + "</channelId>"
                + "<channelOrderId>"
                + orderNo
                + "</channelOrderId>"
                + "<cid>"
                + certNo
                + "</cid>"
                + "<name>"
                + userName
                + "</name>"
                + "<carNo>"
                + carNo
                + "</carNo>"
                + "</subatm>";

        return xml;
    }
}
