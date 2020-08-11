package com.xdream.wisdom.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflenSerType {

    private String servercode;	//服务码
    private String channelid;	//上游产品编号
    private String msgtype;		//详细接口字段
    private String url;			//请求通道
    private String requestflag;	//请求方式（post or get）
    private String valid;  //是否签名
    private String mackey;//秘钥
    private String macdef;//签名字段 多个字段用":"分隔
    private String mactype;//加密方式  MD5 3DES RSA BASE64 暂定4种
    private String childsercode;//子交易码


    //拼包
    public static String  publicPack(Object object){
        // 得到对象的类
        Class c = object.getClass();
        // 得到对象中所有的方法
        Method[] methods = c.getMethods();
        for (Method s:methods) {
            String name = s.getName();
        }
        // 得到对象中所有的属性
        Field[] fields = c.getFields();
        for (Field f:fields) {
            System.out.println(f.toString());
        }
        // 得到对象类的名字
        String cName = c.getName();
        return null;
    }

    public String getChildsercode() {
        return childsercode;
    }

    public void setChildsercode(String childsercode) {
        this.childsercode = childsercode;
    }

    public String getMactype() {
        return mactype;
    }

    public void setMactype(String mactype) {
        this.mactype = mactype;
    }

    public String getMacdef() {
        return macdef;
    }

    public void setMacdef(String macdef) {
        this.macdef = macdef;
    }

    public String getMackey() {
        return mackey;
    }

    public void setMackey(String mackey) {
        this.mackey = mackey;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getRequestflag() {
        return requestflag;
    }

    public void setRequestflag(String requestflag) {
        this.requestflag = requestflag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getChannelid() {
        return channelid;
    }

    public void setChannelid(String channelid) {
        this.channelid = channelid;
    }

    public String getServercode() {
        return servercode;
    }

    public void setServercode(String servercode) {
        this.servercode = servercode;
    }

    //拆包 --listJsonStr


}
