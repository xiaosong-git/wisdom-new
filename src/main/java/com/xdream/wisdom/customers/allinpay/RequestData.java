package com.xdream.wisdom.customers.allinpay;

import java.io.Serializable;

/**
 * Created by CNL on 2020/7/14.
 */
public class RequestData implements Serializable{

    /**
     * 通商云开放平台分配的、用于接口交互的应用ID
     */
    private String appId;

    /**
     * 企业客户的应用ID，透传给服务商
     */
    private String clientAppId;

    /**
     * 接口名称，需要和通商云开放平台输出接口的method保持一致	allinpay.yunst.memberService.updatePhoneByPayPwd
     */
    private String method;

    /**
     * 仅支持JSON
     */
    private String format;

    /**
     * 请求使用的编码格式，utf-8
     */
    private String charset;


    /**
     * 生成签名字符串所使用的签名算法类型，目前支持SHA256WithRSA
     */
    private String signType;


    /**
     * 开放平台请求参数的签名串
     */
    private String sign;


    /**
     * 发送请求的时间，格式"yyyy-MM-dd HH:mm:ss" 2014-07-24 03:07:50
     */
    private String timestamp;

    /**
     * 调用的接口版本	1.0
     */
    private String version;

    /**
     * 通商云服务器主动通知商户服务器里指定的页面http/https路径
     */
    private String notifyUrl;


    /**
     * 请求参数的集合，最大长度不限，除公共参数外所有请求参数都必须放在这个参数中传递
     */
    private String bizContent;


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getClientAppId() {
        return clientAppId;
    }

    public void setClientAppId(String clientAppId) {
        this.clientAppId = clientAppId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getBizContent() {
        return bizContent;
    }

    public void setBizContent(String bizContent) {
        this.bizContent = bizContent;
    }
}
