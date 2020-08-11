package com.xdream.wisdom.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RsaSignCoder {
	private static final Logger log = LoggerFactory.getLogger(Cipher3DES.class);

	public static String sign(String datas, String privates) throws UnsupportedEncodingException {
		byte[] data = datas.getBytes("utf-8");
		byte[] privateKey = CipherBase64.decryptBASE64(privates);

		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
			Signature signature = Signature.getInstance("MD5withRSA");
			signature.initSign(priKey);
			signature.update(data);
			return CipherBase64.encryptBASE64(signature.sign());
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | InvalidKeySpecException e) {
			log.error(e.getMessage());
		}
		return null;

	}

	public static boolean verify(String datas, String publicKeys, String signs) throws UnsupportedEncodingException {
		byte[] data = datas.getBytes("utf-8");
		byte[] publicKey = CipherBase64.decryptBASE64(publicKeys);
		byte[] sign = CipherBase64.decryptBASE64(signs);

		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
			PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
			Signature signature = Signature.getInstance("MD5withRSA");
			signature.initVerify(pubKey);
			signature.update(data);
			return signature.verify(sign);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
			log.error(e.getMessage());
		}
		return false;
	}

	public static String getPrivateKey(Map<String, Object> keyMap) {
		Key key = (Key) keyMap.get("RSAPrivateKey");
		return CipherBase64.encryptBASE64(key.getEncoded());
	}

	public static String getPublicKey(Map<String, Object> keyMap) {
		Key key = (Key) keyMap.get("RSAPublicKey");
		return CipherBase64.encryptBASE64(key.getEncoded());
	}

}
