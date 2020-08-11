package test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.xdream.wisdom.util.SSLClient;

public class Main {
	public static String key_mac ="HmacMD5";
	
	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
		// TODO Auto-generated method stubs
        String inputStr="/v1/bankcard/type/t2\nbankCard=6236681870000628806&name=黄文坤&idCard=350123199705194193&mobile=15880485249\n1563273881686";
        byte[] inputData =inputStr.getBytes();
        String key="7mTTUICVQQB6WrsBCp4L9WstfgI0c0V38zplIQph";
        System.out.println(Main.byteArrayToHexString(Main.encryptHMAC(inputData,key)));
	}
	public static byte[] encryptHMAC(byte[]data,String key) throws NoSuchAlgorithmException, InvalidKeyException {
		SecretKey secretKey = new SecretKeySpec(key.getBytes(),key_mac);
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		return mac.doFinal(data);
	}
	public static String byteArrayToHexString(byte[] b) {
	       StringBuffer sb = new StringBuffer(b.length * 2);
	       for (int i = 0; i < b.length; i++) {
	         int v = b[i] & 0xff;
	         if (v < 16) {
	           sb.append('0');
	         }
	         sb.append(Integer.toHexString(v));
	       }
	       return sb.toString();
	}
	
}
