package com.xdream.wisdom.customers.allinpay;

/**
 * Created by CNL on 2020/7/14.
 */
public enum  ResultCode {

    SUCCESS("1000","接口调用成功"),FAIL("2000","服务不可用");//这个后面必须有分号

    private String code;
    private String name;
    private ResultCode(String code,String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public void setName(String name) {
        this.name = name;
    }

}
