package com.xdream.wisdom.util.encryption;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

import javax.crypto.Cipher;

public class RSAUtil {
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;//秘钥长度除以8减11

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;//秘钥长度除以8




    public static String readFile(String filePath, String charSet) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        try {
            FileChannel fileChannel = fileInputStream.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
            fileChannel.read(byteBuffer);
            byteBuffer.flip();
            return new String(byteBuffer.array(), charSet);
        } finally {
            fileInputStream.close();
        }

    }

    public static String getKey(String string) throws Exception {
        String content = readFile(string, "UTF8");
        return content.replaceAll("\\-{5}[\\w\\s]+\\-{5}[\\r\\n|\\n]", "");
    }

    public static boolean verifyByKeyPath(String content, String sign, String publicKeyPath, String input_charset) {
        try {
            return verifyByPublicKey(content, sign, getKey(publicKeyPath), input_charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static KeyInfo getPFXPrivateKey(String pfxPath, String password)
            throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException, UnrecoverableKeyException {
        FileInputStream fis = new FileInputStream(pfxPath);
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(fis, password.toCharArray());
        fis.close();
        Enumeration<String> enumas = ks.aliases();
        String keyAlias = null;
        if (enumas.hasMoreElements())// we are readin just one certificate.
        {
            keyAlias = enumas.nextElement();
        }

        KeyInfo keyInfo = new KeyInfo();

        PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, password.toCharArray());
        Certificate cert = ks.getCertificate(keyAlias);
        PublicKey pubkey = cert.getPublicKey();

        keyInfo.privateKey = prikey;
        keyInfo.publicKey = pubkey;
        return keyInfo;
    }

    public static class KeyInfo {

        PublicKey publicKey;
        PrivateKey privateKey;

        public PublicKey getPublicKey() {
            return publicKey;
        }

        public PrivateKey getPrivateKey() {
            return privateKey;
        }
    }

    // =====================获取公钥私钥===============================//


    //得到公钥
    public static PublicKey getPublicKey(String key) throws Exception {
        if (key == null) {
            throw new Exception("加密公钥为空, 请设置");
        }
        byte[] buffer = Base64.decode(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        try {
        	 return keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			throw new Exception("输入正确的公钥");
		}
       
    }

    /**
     * 得到私钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes = buildPKCS8Key(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey;
        try {
        	    privateKey = keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
			throw new Exception("秘钥获取失败,请输入正确的私钥");
		}
     
        return privateKey;
    }

    /**
     * 获取公钥
     *
     * @param filename
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKeyFile(String filename) throws Exception {
        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    /**
     * 获取私钥
     *
     * @param filename
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKeyFile(String filename) throws Exception {
        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private static byte[] buildPKCS8Key(String privateKey) throws Exception {
        if (privateKey.contains("-----BEGIN PRIVATE KEY-----")) {
            return Base64.decode(privateKey.replaceAll("-----\\w+ PRIVATE KEY-----", ""));
        } else if (privateKey.contains("-----BEGIN RSA PRIVATE KEY-----")) {
            final byte[] innerKey = Base64.decode(privateKey.replaceAll("-----\\w+ RSA PRIVATE KEY-----", ""));
            final byte[] result = new byte[innerKey.length + 26];
            System.arraycopy(Base64.decode("MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKY="), 0, result, 0, 26);
            System.arraycopy(BigInteger.valueOf(result.length - 4).toByteArray(), 0, result, 2, 2);
            System.arraycopy(BigInteger.valueOf(innerKey.length).toByteArray(), 0, result, 24, 2);
            System.arraycopy(innerKey, 0, result, 26, innerKey.length);
            return result;
        } else {
            return Base64.decode(privateKey);
        }
    }




    // ==========================秘钥加解密================================//

//    //私钥加密 调用方法
//    public static String encryptByPrivate(String content, String privateKey,String input_charset) throws Exception {
//        if (privateKey == null) {
//            throw new Exception("加密私钥为空, 请设置");
//        }
//        PrivateKey privateKeyInfo = getPrivateKey(privateKey);
//        java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
//        signature.initSign(privateKeyInfo);
//        signature.update(content.getBytes(input_charset));
//        return Base64.encode(signature.sign());
//    }
    public static String encryptByPrivate(String data, String privateKey) throws Exception {
    	 byte[] encryptDate = data.getBytes("UTF-8");
         Cipher cipher = Cipher.getInstance("RSA");
         cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey(privateKey));
         int inputLen = encryptDate.length;
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         int offSet = 0;
         byte[] cache;
         int i = 0;
         // 对数据分段加密
         while (inputLen - offSet > 0) {
             if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                 cache = cipher.doFinal(encryptDate, offSet, MAX_ENCRYPT_BLOCK);
             } else {
                 cache = cipher.doFinal(encryptDate, offSet, inputLen - offSet);
             }
             out.write(cache, 0, cache.length);
             i++;
             offSet = i * MAX_ENCRYPT_BLOCK;
         }
         byte[] encryptedData = out.toByteArray();
         out.close();

         return Base64.encode(encryptedData);
    }
    /**公钥解密 调用方法*/
    public static String decryptByPublic(String encryptedData, String publicKey) throws Exception {
        if (publicKey == null) {
            throw new Exception("解密公钥为空, 请设置");
        }
        byte [] encrypt=Base64.decode(encryptedData);
        PublicKey publicKeyInfo = getPublicKey(publicKey);
        Cipher cipher=Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE,publicKeyInfo);

        int inputLen=encrypt.length;
        int offSet=0;
        byte[]cache;
        int i=0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (inputLen - offSet>0){

            if (inputLen-offSet>MAX_DECRYPT_BLOCK){
                cache=cipher.doFinal(encrypt,offSet,MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encrypt, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData);
    }
    //============================================================//


    /**
     * RSA验签名检查
     * @param content       待签名数据
     * @param sign          签名值
     * @param publicKey     公钥
     * @param input_charset 编码格式
     * @return 布尔值
     *///使用私钥加密 公钥解密验证 调用方法
    public static boolean verifyByPublicKey(String content, String sign, String publicKey, String input_charset) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decode(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            java.security.Signature signature = java.security.Signature .getInstance(SIGN_ALGORITHMS);
            signature.initVerify(pubKey);
            signature.update(content.getBytes(input_charset));
            boolean bverify = signature.verify(Base64.decode(sign));
            return bverify;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    //==========================明文加解密/签名验签=====================================//
    /**
     * 私钥解密
     *接收方解密
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String encryptedData, String privateKey) throws Exception {

        byte[] encrypt = Base64.decode(encryptedData);//转换加密数据类型转换
        PrivateKey privateK = getPrivateKey(privateKey);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encrypt.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encrypt, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encrypt, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData);
    }

    /**
     * 公钥加密
     *  请求方加密
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, String publicKey) throws Exception {
        byte[] encryptDate = data.getBytes("UTF-8");
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        int inputLen = encryptDate.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(encryptDate, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptDate, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();

        return Base64.encode(encryptedData);
    }

    /**
     * 公钥加密，私钥验证
     * @param content       未加密的数据
     * @param sign          加密后的数据
     * @param privateKey    私钥
     * @return
     * @throws Exception
     */
    public static boolean verifyByPrivateKey(String content, String sign, String privateKey) throws Exception {
        String data = decryptByPrivateKey(sign, privateKey);
        if (content.equals(data)) {
            return true;
        }
        return false;
    }

}
