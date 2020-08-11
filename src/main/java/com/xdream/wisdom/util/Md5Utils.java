package com.xdream.wisdom.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Utils {

    public static final String HASH_ALGORITHM_NAME = "MD5";

    private static final String CHAR_ENCODING = "UTF-8";


    /**
     * 对传入的字符串做MD5哈希处理，返回大写结果
     *
     * @param source
     * @return
     */
    public static String md5WithUpperCase(String source) {
        return md5(source, false);
    }

    /**
     * 对传入的字符串做MD5哈希处理，返回小写结果
     *
     * @param source
     * @return
     */
    public static String md5WithLowerCase(String source) {
        return md5(source, true);
    }

    /**
     * 对传入的字符串做MD5哈希处理
     *
     * @param source
     * @param useLowerCase
     * @return
     */
    private static String md5(String source, boolean useLowerCase) {
        try {
            MessageDigest md5 = MessageDigest.getInstance(HASH_ALGORITHM_NAME);
            return HexUtils.encode(md5.digest(source.getBytes(CHAR_ENCODING)), useLowerCase);
        } catch (NoSuchAlgorithmException e) {
            // Formalization, should not occurs actually.
            throw new IllegalStateException("No such algorithm which named 'MD5'", e);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unsupported encoding named 'UTF-8'", e);
        }
    }
}
