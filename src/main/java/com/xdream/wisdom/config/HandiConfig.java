package com.xdream.wisdom.config;

public class HandiConfig {

    /** 分配给商户的渠道号 */
    public static final String CHANNELID = "11008016";
    /** 请求url */
    public static final String URL = "http://testds.handydata.cn:8010/dataservice/service.ac";
    /** 分配给商户的公钥 */
    public static final String PUBLICKEY = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIW2q9ECZCNRNkzUIuAEy7JsRvvw1DPyI3BtfYpa7R9vgWV9rVxbMXR533eaz76dp3CL9hzcEwbTPBCfCdxSU5UCAwEAAQ==";

    /** 请求version */
    public static final String VERSION = "1.0.0";
    //交易代码不良信息
    public static final String TRANSCODE = "101000";
    //手机 身份证 姓名 验证
    public static final String PHONE_TRANSCODE = "100064";
    //行驶证查询
    public static final String DRIVING_TRANSCODE = "300648";
    //驾驶证查询
    public static final String DRIVINGPLUS_TRANSCODE = "300647";
    //重点人员拓展信息查询
    public static final String DEVELOPMENT_TRANSCODE = "300646";
    //重点人员核验plus查询
    public static final String DEVELOPMENTPLUS_TRANSCODE = "300645";
    //应用名称
    public static final String APPLICATION = "GwBiz.Req";

}
