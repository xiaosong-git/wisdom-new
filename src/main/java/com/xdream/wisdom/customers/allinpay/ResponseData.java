package com.xdream.wisdom.customers.allinpay;

import java.io.Serializable;

/**
 * Created by CNL on 2020/7/14.
 */
public class ResponseData implements Serializable{


    /**
     * 服务商接口返回码
     */
    private String code;

    /**
     * 服务商接口返回码描述
     */
    private String msg;

    /**
     * 签名
     */
    private String sign;

    /**
     *返回参数的集合，最大长度不限，除公共参数外所有返回参数都必须放在这个参数中传递
     */
    private String data;


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
