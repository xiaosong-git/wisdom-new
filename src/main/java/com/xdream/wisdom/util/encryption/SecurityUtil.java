package com.xdream.wisdom.util.encryption;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class SecurityUtil {

	/** 使用私钥加密 */
	public static String enByPriKey(Object object, String key) throws Exception {
		String data=	String.valueOf(object);
		if (StringUtils.isBlank(data)||"null".equals(data)) {
			return null;
		}
		try {
			return RSAUtil.encryptByPrivate(data, key);
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 使用秘钥 加密
	 * 
	 * @param map
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> enByPriKey(Map<String, Object> map,String key) throws Exception {

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			try {
				map.put(entry.getKey(), RSAUtil.encryptByPrivate(String.valueOf(entry.getValue()), key));

			} catch (Exception e) {
				return null;
			}
			
		}

		return map;
	}

	/** 使用公钥解密 */
	public static String deByPubKey(Object object, String key) throws Exception {
		String data=	String.valueOf(object);
		if (StringUtils.isBlank(data)||"null".equals(data)) {
			return null;
		}
		try {
			return RSAUtil.decryptByPublic(data, key);
		} catch (Exception e) {
			return null;
		}
		

	}

	/**
	 * 使用公钥解密
	 * 
	 * @param map
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> deByPubKey(Map<String, Object> map,String key) throws Exception {

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			try {
				map.put(entry.getKey(), RSAUtil.decryptByPublic(String.valueOf(entry.getValue()), key));

			} catch (Exception e) {
				return null;
			}
			
		}
		return map;
	}

	/** 使用公钥钥加密(非对称加密RSA) */
	public static String enByPubKey(Object object, String key) throws Exception {
		String data=	String.valueOf(object);
		if (StringUtils.isBlank(data)||"null".equals(data)) {
			return null;
		}
		try {
			return RSAUtil.encryptByPublicKey(data, key);
		} catch (Exception e) {
			return null;
		}	
	}

	/**
	 * 使用公钥钥加密(非对称加密RSA)
	 * 
	 * @param map
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> enByPubKey(Map<String, Object> map,String key) throws Exception {

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			try {
				map.put(entry.getKey(), RSAUtil.encryptByPublicKey(String.valueOf(entry.getValue()), key));
			} catch (Exception e) {
				// TODO: handle exception
				return  null;
					
			}
			
		}
		return map;
	}

	/**
	 * 使用秘钥解密(非对称解密RSA)
	 * @param map
	 * @param key  平台秘钥
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> deByPriKey(Map<String, Object> map,String key) throws Exception {

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			try {

				map.put(entry.getKey(),RSAUtil.decryptByPrivateKey(String.valueOf(entry.getValue()), key));
		
			} catch (Exception e) {
				return null;	
			}

		}

		return map;
	}

	/** 使用私钥解密  使用秘钥解密(非对称解密RSA) */
	public static String deByPriKey(Object object, String key) throws Exception{
		String data=	String.valueOf(object);
		if (StringUtils.isBlank(data)||"null".equals(data)) {
			return null;
		}
		try {
			return RSAUtil.decryptByPrivateKey(data, key);
			} catch (Exception e) {

		return null;
	}
		
		
	}

	/**
	 * 获取签名值 md5
	 * @param signSource 需要签名的字符串
	 * @param //key
	 * @return
	 * @throws Exception
	 */
	public static String getSign(String signSource)throws Exception {
		
		if (StringUtils.isBlank(signSource)) {
			return null;
		}
		try {
			return MD5Util.MD5Encode(signSource,"UTF-8");
		} catch (Exception e) {
			return null;
		}
		
	}

	/**
	 * 验证签名是否正确  md5
	 * @param sign 	 签名值
	 * @param signSource   需要签名的字符串	
	 * @param //key
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(String sign, String signSource)throws Exception {
		System.out.println("字符串："+signSource);
		if (StringUtils.isBlank(sign)||StringUtils.isBlank(signSource)) {
			return false;
		}
		String newSign = MD5Util.MD5Encode(signSource,"UTF-8");
		System.out.println("mac:="+sign);
		System.out.println("newmac  zzz:="+newSign);
		if (newSign != null && newSign.equals(sign)) {

			return true;
		}
		return false;
	}

	/***
	 * 用于单个字符串加密
	 * 3DES加密
	 * @param data
	 * @param key 秘钥
	 *            
	 * @return 返回HEX串
	 * @throws Exception
	 */
	public static String encodeString(String data, String key) throws Exception {
		
		if ("".equals(key)||null==key||key.length()<8) {
			throw new Exception("秘钥不可为空");
		}	
		return DESUtil.encode(key, data);
	}
	/***
	 * 用于单个字符串解密
	 * 3DES解密
	 * @param data
	 * @param key 秘钥
	 *            
	 * @return 返回HEX串
	 * @throws Exception
	 */
	public static String decodeString(String data, String key) throws Exception {
		if ("".equals(key)||null==key||key.length()<8) {
			throw new Exception("秘钥不可为空");
		}
		return DESUtil.decode(key, data);
	}
	/**
	 * 签名2
	 * @param text
	 * @param key
	 * @param input_charset
	 * @return
	 * @throws Exception
	 */
	public static String sign1(String text, String key, String input_charset)throws Exception{
		try {	
		return 	MD5Util.sign1(text, key, input_charset);
		} catch (Exception e) {	
			e.printStackTrace();
			return null;
		}
	}
	
	/**签名2 md5
	 * @param sign 	 签名值
	 * @param signSource   需要签名的字符串	
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static boolean verify1(String sign, String signSource, String key)throws Exception {
		if ("".equals(key)||null==key||key.length()<8) {
			throw new Exception("签名为空，或秘钥长度不小于8位");
		}		
		if (StringUtils.isBlank(sign)||StringUtils.isBlank(signSource)) {
			return false;
		}
		String newSign = MD5Util.sign1(signSource, key, "UTF-8");
		if (newSign != null && newSign.equals(sign)) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) throws Exception {
		String newSign = MD5Util.MD5Encode("10000000070000101568894114549A0723248F21943R4208534528919630","UTF-8");
		//enByPriKey
//		Map<String,Object> map=new HashMap<>();
//		map.put("custid","1000000004");
//		map.put("productcode","000006");
//		map.put("serialno","156889411112");
//		map.put("public")
		System.out.println(newSign);
		System.out.println(System.currentTimeMillis());
	}

}
