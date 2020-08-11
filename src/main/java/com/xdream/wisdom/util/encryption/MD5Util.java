package com.xdream.wisdom.util.encryption;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import org.apache.commons.codec.digest.DigestUtils;


public class MD5Util {
	//MD5小写
    public final static String MD5(String s,String encodingType) throws Exception{
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        try {
            byte[] btInput = s.getBytes(encodingType);
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public MD5Util()
    {
    }

    private static String byteArrayToHexString(byte b[])throws Exception {
        StringBuffer resultSb = new StringBuffer();
        for(int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b)throws Exception{
        int n = b;
        if(n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }
    //MD5取大写
    public static String MD5Encode(String origin)throws Exception{
        String resultString = null;
        try
        {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString.getBytes())).toUpperCase();
        }
        catch(Exception exception) { }
        return resultString;
    }
    public static String MD5Encode(String origin,String charset)throws Exception {
        String resultString = null;
        try
        {
            //resultString = new String(origin.getBytes(charset));
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(origin.getBytes(charset))).toUpperCase();
        }
        catch(Exception exception) { }
        return resultString;
    }
    public static String MD5Encode2(String origin,String charset)throws Exception {
        String resultString = null;
        try
        {
            //resultString = new String(origin.getBytes(charset));
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(origin.getBytes(charset)));
        }
        catch(Exception exception) { }
        return resultString;
    }
    private static final String hexDigits[] = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f"
    };

    /**
     * 签名字符串
     * @param text 需要签名的字符串
     * @param key 密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static String sign1(String text, String key, String input_charset) {
    	
        return DigestUtils.md5Hex(getContentBytes(text, input_charset));
    }
    /**
     * @param content
     * @param charset
     * @return
     * @throws SignatureException
     * @throws UnsupportedEncodingException 
     */
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }
    
    /**
     * 思空md5加密算法
     * @param text
     * @return
     */
    public static String md5(String text) {
		return md5(new String[]{text});
	}

	public static String md5(String[] text) {
		byte[] bytes = digest(text);
		return new String(encodeHex(bytes));
	}
	/**
	 * 前64位转换为long
	 */
	public static long halfDigest(String... text) {
		long ret = 0;
		byte[] bytes = digest(text);
		for (int i=0; i<8; i++)
			ret = ret << 8 | (bytes[i] & 0xFFL);
		return ret;
	}

	public static byte[] digest(String... text) {
		MessageDigest msgDigest = null;
		try {
			msgDigest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(
					"System doesn't support MD5 algorithm.");
		}

		try {
			for (String str : text) {
				msgDigest.update(str.getBytes("utf-8"));
			}

		}
		catch (UnsupportedEncodingException e) {

			throw new IllegalStateException(
					"System doesn't support your  EncodingException.");

		}

		return msgDigest.digest();
	}

	// 16位的MD5就是32位的中间的是内容
	public static String md5_16(String text) {
		return md5(text).substring(8, 24);
	}

	public static char[] encodeHex(byte[] data) {
		char DIGITS[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
		int l = data.length;

		char[] out = new char[l << 1];

		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS[0x0F & data[i]];
		}
		return out;
	}
	
	public static void main(String[] args)throws Exception {
		
	}	
    
    
		}
