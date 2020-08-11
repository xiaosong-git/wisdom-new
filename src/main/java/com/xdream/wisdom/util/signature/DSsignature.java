package com.xdream.wisdom.util.signature;

import java.util.Map;

import org.apache.commons.codec.binary.Hex;

import com.aliyun.openservices.shade.org.apache.commons.codec.binary.Base64;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.xdream.wisdom.util.JacksonUtils;
import com.xdream.wisdom.util.encryption.AesUtils;
/**
 * 
 * @author Soap
 * 重点人员签名
 */
public class DSsignature {
     public static String sign(Map<String,String> maps,String enKey) throws Exception {
    	 String msgData = JacksonUtils.serialObject(maps);
    	 byte[] data = AesUtils.encrypt(msgData.getBytes(),enKey.getBytes(),"ds");
    	 byte[] b64 = Base64.encodeBase64(data);	
    	 char[] hex = Hex.encodeHex(b64);
    	 System.out.println("加密:" + new String(hex));
    	 
    	 byte[] hex2 = Hex.decodeHex(hex);
         byte[] b64_2 = Base64.decodeBase64(hex2);
         byte[] data2 = AesUtils.dencrypt(b64_2, enKey.getBytes(), "ds");
         System.out.println("解密:" + new String(data2));
         
         String signData=new String(hex);	
    	 return signData;
     }
    
}
