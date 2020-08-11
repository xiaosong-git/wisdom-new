package com.xdream.wisdom.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.allinpay.mcp.comm.AllinpayUtils;
import com.xdream.wisdom.config.JinDieConfig;
import com.xdream.wisdom.entity.ChannelSer;
import com.xdream.wisdom.entity.JSonSerDep;
import com.xdream.wisdom.entity.ReqData;
import com.xdream.wisdom.util.encryption.Base64;
import com.xdream.wisdom.util.encryption.DESUtil;
import com.xdream.wisdom.util.encryption.MD5Util;
import com.xdream.wisdom.util.encryption.RSAUtil;
import com.xdream.wisdom.util.signature.BLsignature;
import com.xdream.wisdom.util.signature.DSsignature;

import java.util.*;

public class SerPackageUtil {

	/**
	 * json报文拼包
	 *
	 * @param //chanSer
	 * @param //msgBody
	 * @param //jsondep
	 * @return
	 * @throws Exception
	 */
	private static String getsignParam(JSONObject json) {
		StringBuffer signBuffer = new StringBuffer();
		TreeMap<String, Object> jsonMap = JSON.parseObject(json.toJSONString(),
				new TypeReference<TreeMap<String, Object>>() {
				});
		for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
			signBuffer.append(entry.getKey() + "=" + entry.getValue() + "&");

		}
		return signBuffer.toString().substring(0, signBuffer.toString().length() - 1);
	}


	public static String packJsonMsg(ChannelSer chanSer, Map<String, String> msgBody, List<JSonSerDep> jsondep)
			throws Exception {
		String validl = chanSer.getValid();// 是否签名
		String mackey = chanSer.getMackey();// 秘钥
		String macdef = chanSer.getMacdef();// 加密字段
		String mactype = chanSer.getMactype();// 签名方式
		JSONObject jsonObject = new JSONObject();


		if(chanSer.getChannelid().equals("WJT0001")){
			Map<String,String> param = new HashMap();
			param.put("merId", "647022261975646208");
			param.put("name", msgBody.get("Name"));
			param.put("idCard", msgBody.get("idCard"));
			param.put("certType", "0");
			param.put("bankNo", msgBody.get("bankNo"));
			param.put("timestamp",String.valueOf(System.currentTimeMillis()));
			param.put("custOrderId",String.valueOf(System.currentTimeMillis()));
			param.put("tradScene","商户");
			param.put("tradCustomerName","小松智信");
			return JSON.toJSONString(param);
		}


        if(chanSer.getChannelid().equals("WJT0002")){
            Map<String,String> param = new HashMap();//custOrderId  merId  timestamp  name  phoneNo  bankNo  idCard certType accountType
            param.put("merId", "647022261975646208");
            param.put("name", msgBody.get("Name"));
            param.put("idCard", msgBody.get("idCard"));
            param.put("certType", "0");
            param.put("bankNo", msgBody.get("bankNo"));
            param.put("phoneNo",msgBody.get("phoneNo"));
            param.put("timestamp",String.valueOf(System.currentTimeMillis()));
            param.put("custOrderId",String.valueOf(System.currentTimeMillis()));
            param.put("accountType","1");
            return JSON.toJSONString(param);
        }


		//String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCybCbJJO738hGsphvxSsS7kipp4YGu7jXRkTWFS608LnToOVdixYVv1ROI8xhgfxtCWwoZD3VkoKg1X3w5jZvv1M0hHfGGqqHt+NF9pWUpim1dBxv/qj3nW6b5YDbiEeBFnZy8RlZAr0P9lhxaiDIZtfMMjyTHC4YmZlfMiGAgmwIDAQAB";
		//金蝶接口 拼接data
		if(chanSer.getChannelid().startsWith("JD")){
			String vector = UUID.randomUUID().toString().substring(0, 8);
			String appid = JinDieConfig.APPID;
			JSONObject json = new JSONObject();
			msgBody.put("client_id",appid);
			msgBody.put("client_secret",JinDieConfig.CLIENT_SECRET);

			JSONObject itemJSONObj = JSONObject.parseObject(JSON.toJSONString(msgBody));

			StringBuffer signBuffer = new StringBuffer();
			TreeMap<String, Object> jsonMap = JSON.parseObject(itemJSONObj.toJSONString(),
					new TypeReference<TreeMap<String, Object>>() {
					});
			for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
				signBuffer.append(entry.getKey() + "=" + entry.getValue() + "&");
				json.put(entry.getKey(),entry.getValue());
			}
			String substring = signBuffer.toString().substring(0, signBuffer.toString().length() - 1);

			String sign = RsaSignCoder.sign(substring, mackey);
			json.put("sign",sign);
			String encrData = Cipher3DES.encrypt(json.toJSONString(), JinDieConfig.DK, vector);
			ReqData data = new ReqData();
			data.setAppid(appid);
			data.setData(encrData);
			data.setVector(vector);
			return JSON.toJSONString(data);

		}

		String signKey = "";// 签名字段
		// 拼接非签名字段
		for (int i = 0; i < jsondep.size(); i++) {
			JSonSerDep jsp = jsondep.get(i);
			String key = jsp.getImpmsg();
			String value = msgBody.get(key);
			String ismac = jsp.getIsmac();// 是否是签名字段0-可输，1-必输
			String defultValue = jsp.getDefvalue();// 默认值不为空的都必输
			if ("0".equals(ismac)) {
				signKey = key; // 签名信息最后拼
			} else {
				if (null != defultValue && !"".equals(defultValue)) {// 默认值不为空的必送
					jsonObject.put(key, defultValue);
				} else {
					if (null != value && !"".equals(value)) {
						jsonObject.put(key, value);
					}
				}

			}
		}
		// 签名字段拼接
		if ("0".equals(validl) && !"".equals(signKey)) {
			String signStr = "";
			if (chanSer.getChannelid().equals("BL0002")) {
				Map<String, String> predata = new HashMap<String, String>();
				predata.put("productCode", "8204");//8202
				predata.put("cid", msgBody.get("cid"));
				predata.put("name", msgBody.get("name"));
				predata.put("subChannelName", "福建小松安信信息科技有限公司");
				predata.put("channelOrderId", String.valueOf(System.currentTimeMillis()));
				signStr = DSsignature.sign(predata, chanSer.getMackey());
			} else {
				String[] signdef = macdef.split(":");
				StringBuffer bf = new StringBuffer();
				for (int t = 0; t < signdef.length; t++) {
					if (signdef[t].startsWith("\""))// 默认值以双引号开通头
					{
						bf.append(signdef[t].replace("\"", ""));
					} else {
						bf.append(jsonObject.get(signdef[t]));
					}

				}

				signStr = bf.toString();
				// 签名方式MD5 3DES RSA BASE64 暂定4种
				if ("1".equals(mactype)) {
					signStr = MD5Util.MD5(signStr, "UTF-8");
				} else if ("2".equals(mactype)) {
					signStr = DESUtil.encode(mackey, signStr);

				} else if ("3".equals(mactype)) {
					signStr = RSAUtil.encryptByPublicKey(signStr, mackey);
				} else {
					signStr = Base64.encode(signStr.getBytes("UTF-8"));
				}
			}
			jsonObject.put(signKey, signStr);
		}
		return jsonObject.toJSONString();
	}

	/**
	 * form报文拼包
	 *
	 * @param chanSer
	 * @param msgBody
	 * @param jsondep
	 * @return
	 * @throws Exception
	 */
	public static String packFormMsg(ChannelSer chanSer, Map<String, String> msgBody, List<JSonSerDep> jsondep)
			throws Exception {
		String valid = chanSer.getValid();// 是否签名
		String mackey = chanSer.getMackey();// 秘钥
		String macdef = chanSer.getMacdef();// 加密字段
		String mactype = chanSer.getMactype();// 签名方式
		Map<String, String> maps = new TreeMap<String, String>();
		// params存放form内容
		String params = "";
		JSONObject jsonObject = new JSONObject();
		String signKey = "";// 签名字段
		// 拼接非签名字段
		for (int i = 0; i < jsondep.size(); i++) {
			JSonSerDep jsp = jsondep.get(i);
			String key = jsp.getImpmsg();
			String value = msgBody.get(key);
			String ismac = jsp.getIsmac();// 是否是签名字段0-可输，1-必输
			String defaultValue = jsp.getDefvalue();// 默认值不为空的都必输
			if ("0".equals(ismac)) {
				signKey = key; // 签名信息最后拼
			} else {
				if (!"".equals(defaultValue) && defaultValue != null) {// 默认值不为空的必送
					maps.put(key, defaultValue);
					jsonObject.put(key, defaultValue);
					// value=URLEncoder.encode(defaultValue,"utf-8");
					params += key + "=" + defaultValue + "&";
				} else if (null != value && !"".equals(value)) {
					maps.put(key, value);
					jsonObject.put(key, value);
					// value=URLEncoder.encode(value,"utf-8");
					params += key + "=" + value + "&";
				}

			}
		}
		// 签名字段上送
		if ("0".equals(valid) && !"".equals(signKey)) {
			String signStr = "";
			// 根据不同通道的id，调用不同的签名方式
			if (chanSer.getChannelid().equals("BL0001")) {
				signStr = BLsignature.sign(maps) + "key=" + mackey;
				signStr = MD5Util.MD5Encode(signStr, "UTF-8");
			} else {
				String[] signdef = macdef.split(":");
				StringBuffer bf = new StringBuffer();
				for (int t = 0; t < signdef.length; t++) {
					if (signdef[t].startsWith("\""))// 默认值以双引号开通头
					{
						bf.append(signdef[t].substring(1, signdef[t].length() - 1));
					} else {
						bf.append(jsonObject.get(signdef[t]));
					}

				}
				signStr = bf.toString();

				// 签名方式MD5 3DES RSA BASE64 暂定4种
				if ("1".equals(mactype)) {
					signStr = MD5Util.MD5Encode(signStr, "UTF-8");
				} else if ("2".equals(mactype)) {
					signStr = DESUtil.encode(mackey, signStr);

				} else if ("3".equals(mactype)) {
					signStr = RSAUtil.encryptByPublicKey(signStr, mackey);
				} else {
					signStr = Base64.encode(signStr.getBytes("UTF-8"));
				}
			}
			jsonObject.put(signKey, signStr);
			params += signKey + "=" + signStr;
		} else {
			params = params.substring(0, params.length() - 1);
		}

		return params;
	}

	/**
	 * xml报文拼包
	 *
	 * @param chanSer
	 * @param msgBody
	 * @param jsondep
	 * @return
	 * @throws Exception
	 */
	public static String packXmlMsg(ChannelSer chanSer, Map<String, String> msgBody, List<JSonSerDep> jsondep)
			throws Exception {
		String validl = chanSer.getValid();// 是否签名
		String mackey = chanSer.getMackey();// 秘钥
		String macdef = chanSer.getMacdef();// 加密字段
		String mactype = chanSer.getMactype();// 签名方式
		JSONObject jsonObject = new JSONObject();
		// params存放xml内容
		String params = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		String signKey = "";// 签名字段
		// 拼接非签名字段
		for (int i = 0; i < jsondep.size(); i++) {
			JSonSerDep jsp = jsondep.get(i);
			String key = jsp.getImpmsg();
			String value = msgBody.get(key);
			String ismac = jsp.getIsmac();// 是否是签名字段0-可输，1-必输
			String defultValue = jsp.getDefvalue();// 默认值不为空的都必输
			if ("0".equals(ismac)) {
				signKey = key; // 签名信息最后拼
			} else {
				if (null != defultValue && !"".equals(defultValue)) {// 默认值不为空的必送
					jsonObject.put(key, defultValue);
					params = params + "<" + key + ">" + defultValue + "</" + key + ">";
				} else {
					if (null != value && !"".equals(value)) {
						jsonObject.put(key, value);
						params = params + "<" + key + ">" + value + "</" + key + ">";
					}
				}

			}
		}
		// 签名字段上送
		if ("0".equals(validl) && !"".equals(signKey)) {
			String signStr = "";
			String[] signdef = macdef.split(":");
			StringBuffer bf = new StringBuffer();
			for (int t = 0; t < signdef.length; t++) {
				if (signdef[t].startsWith("\""))// 默认值以双引号开通头
				{
					bf.append(signdef[t].replace("\"", ""));
				} else {
					bf.append(jsonObject.get(signdef[t]));
				}

			}
			signStr = bf.toString();
			// 签名方式MD5 3DES RSA BASE64 暂定4种
			if ("1".equals(mactype)) {
				signStr = MD5Util.MD5(signStr, "UTF-8");
			} else if ("2".equals(mactype)) {
				signStr = DESUtil.encode(mackey, signStr);

			} else if ("3".equals(mactype)) {
				signStr = RSAUtil.encryptByPublicKey(signStr, mackey);
			} else {
				signStr = Base64.encode(signStr.getBytes("UTF-8"));
			}
			jsonObject.put(signKey, signStr);
			params += "<" + signKey + "/>" + signStr + "</" + signKey + ">";

		}

		return params;
	}
}
