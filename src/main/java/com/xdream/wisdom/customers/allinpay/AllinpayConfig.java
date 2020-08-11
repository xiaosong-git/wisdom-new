package com.xdream.wisdom.customers.allinpay;
import com.xdream.wisdom.util.encryption.Base64;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Properties;

@Component
public class AllinpayConfig {

    /**
     * 商户号
     */
    public String custid="";
    /**
     * 商户秘钥
     */
    public String secret="";

    private static AllinpayConfig allinpayConfig = null;

    public static AllinpayConfig getInstance()
    {
        if(allinpayConfig == null)
        {
            allinpayConfig = new AllinpayConfig();
        }
        return allinpayConfig;
    }


    public AllinpayConfig()
    {
        readProperty();
    }
    /**
     * 加载配置信息
     */
    private  void readProperty() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("wisdom.properties");
        Properties p = new Properties();
        try {
            p.load(inputStream);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        custid =  p.getProperty("custid");
        secret = p.getProperty("secret");
    }



}
