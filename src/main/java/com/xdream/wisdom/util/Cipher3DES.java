package com.xdream.wisdom.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cipher3DES {
	private final static Logger log = LoggerFactory.getLogger(Cipher3DES.class);
	
	public static String encrypt(String toEncode, String key, String vector){
		try {
			Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
			DESedeKeySpec dks = new DESedeKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory
					.getInstance("DESede");
			SecretKey securekey = keyFactory.generateSecret(dks);
			IvParameterSpec iv = new IvParameterSpec(vector.getBytes(), 0,
					cipher.getBlockSize());
			cipher.init(1, securekey, iv);
			byte[] encoded = cipher.doFinal(toEncode.getBytes("UTF-8"));
			return CipherBase64.encryptBASE64(encoded);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidKeyException | InvalidKeySpecException
				| InvalidAlgorithmParameterException
				| IllegalBlockSizeException | BadPaddingException
				| UnsupportedEncodingException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	public static String decrypt(String toDecode, String key, String vector) {
		try {
			Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
			DESedeKeySpec dks = new DESedeKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
			SecretKey securekey = keyFactory.generateSecret(dks);
			IvParameterSpec iv = new IvParameterSpec(vector.getBytes(), 0,
					cipher.getBlockSize());
			cipher.init(2, securekey, iv);
			byte[] todecodeBytes = CipherBase64.decryptBASE64(toDecode);
			String decoded = new String(cipher.doFinal(todecodeBytes), "utf-8");
			return decoded;
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidKeySpecException | InvalidAlgorithmParameterException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
			log.error(e.getMessage());
		}
		return null;
	}
}
