package com.xdream.wisdom.util;

import java.util.Random;
import java.util.UUID;

public class OrderNoUtil {

    public static String genOrderNo(String prefix,int len){

        IdWorker worker2 = new IdWorker(2);
        long nextId = worker2.nextId();
        int nextIdLen = Long.toString(nextId).length();
        if (len<nextIdLen)
            return nextId+"";

        len = len - nextIdLen;
        //prefix = StringUtil.makeLengthWith0InFront(prefix, len);
        int prefixLen = prefix.length();
        String temp = "";
        if (len>prefixLen){
            temp = getRandomString(len-prefixLen);
        }
        return temp+prefix+nextId;
    }

    private static String getRandomString(int length) { //length表示生成字符串的长度

        String baseChar= UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();//"ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String baseNum="0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if(i%2==0){
                int number = random.nextInt(baseNum.length());
                sb.append(baseNum.charAt(number));
            }else{
                int number = random.nextInt(baseChar.length());
                sb.append(baseChar.charAt(number));
            }
        }
        return sb.toString();
    }

    public static String genOrderNo4Pre(String prefix,int len){


        int prefixLen = prefix.length();

        String temp = "";
        if (len>prefixLen){
            temp = getRandomString(len-prefixLen);
        }
        return prefix + temp;
    }


    public static void main(String[] args) {
        System.out.println("OrderNoUtil:"+OrderNoUtil.genOrderNo4Pre("", 24));
    }
}
