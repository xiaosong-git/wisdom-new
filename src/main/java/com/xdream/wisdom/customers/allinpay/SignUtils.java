package com.xdream.wisdom.customers.allinpay;

import com.xdream.wisdom.util.encryption.Base64;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.util.Enumeration;

/**
 * Created by CNL on 2020/7/29.
 */
public class SignUtils {


    private  PrivateKey privateKey;
    private  Provider provider = new BouncyCastleProvider();

    public SignUtils(String certPath,String certPwd)
    {
        try {
            privateKey = (RSAPrivateKey)loadPrivateKey((String)null, certPath, certPwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public  String sign(String signedValue)
    {
        String sign = null;
        try {
            sign = sign(privateKey, signedValue, "SHA256WithRSA");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }


    private  PrivateKey loadPrivateKey(String alias, String path, String password) throws Exception {
        FileInputStream ksfis = null;

        PrivateKey var11;
        try {
            KeyStore ks = KeyStore.getInstance("pkcs12");
            ksfis = new FileInputStream(path);
            char[] storePwd = password.toCharArray();
            char[] keyPwd = password.toCharArray();
            ks.load(ksfis, storePwd);
            if(StringUtils.isBlank(alias)) {
                Enumeration<String> aliases = ks.aliases();
                if(aliases.hasMoreElements()) {
                    alias = (String)aliases.nextElement();
                }
            }

            var11 = (PrivateKey)ks.getKey(alias, keyPwd);
        } finally {
            if(ksfis != null) {
                ksfis.close();
            }
        }
        return var11;
    }



    private  String sign(PrivateKey privateKey, String text, String algorithm) throws Exception {
        Signature signature = Signature.getInstance(algorithm, provider);
        signature.initSign(privateKey);
        signature.update(text.getBytes("utf8"));
        byte[] data = signature.sign();
        return Base64.encode(data);
    }


}
