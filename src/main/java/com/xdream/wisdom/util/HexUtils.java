package com.xdream.wisdom.util;

public class HexUtils {

    private final static String HEX_DIGITS_STRING = "0123456789ABCDEF";

    private final static char[] LOWERCASE_HEX_DIGITS = HEX_DIGITS_STRING.toLowerCase().toCharArray();

    private final static char[] UPPERCASE_HEX_DIGITS = HEX_DIGITS_STRING.toCharArray();


    public static String encodeWithLowerCase(byte[] data) {
        return encode(data, true);
    }

    public static String encodeWithUpperCase(byte[] data) {
        return encode(data, false);
    }

    /**
     * 转换字节数组为16进制字串
     *
     * @param data 字节数组
     * @return 16进制字串
     */
    public static String encode(byte[] data, boolean useLowercase) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            result.append(encode(data[i], useLowercase));
        }
        return result.toString();
    }

    /**
     *
     * @param hexString 必须以两位16进制为一个单位
     * @return
     */
    public static byte[] decode(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (decode(hexChars[pos]) << 4 | decode(hexChars[pos + 1]));

        }
        return d;
    }


    //---------------------------------------------------------------------
    //
    //---------------------------------------------------------------------

    private static byte decode(char c) {
        return (byte) HEX_DIGITS_STRING.indexOf(c);
    }

    private static String encode(byte b, boolean useLowercase) {
        char[] hexDigitsToUse = useLowercase ? LOWERCASE_HEX_DIGITS : UPPERCASE_HEX_DIGITS;
        int i = b;
        if (i < 0)
            i = 256 + i;
        int d1 = i / 16;
        int d2 = i % 16;
        return String.valueOf(hexDigitsToUse[d1]) + hexDigitsToUse[d2];
    }
}
