package com.cmwebgame.util;

import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;

public class ThreeDes {
	protected final static Logger logger = Logger.getLogger(ThreeDes.class);
	
	private static final String Algorithm = "DESede"; //定义 加密算法,可用 DES,DESede,Blowfish

	//keybyte为加密密钥，长度为24字节
	//src为被加密的数据缓冲区（源）
	public static byte[] encryptMode(String keyStr, byte[] src) {
		try {
			//生成密钥
			Key key;
	        KeyGenerator generator = KeyGenerator.getInstance(Algorithm);
	        //generator.init(new SecureRandom("testkey".getBytes(charset)));//linux環境下會每次會不同，windows不會??
	        //SecureRandom 實現嘗試完全隨機化生成器本身的內部狀態，除非調用方在調用 getInstance 方法之後又調用了 setSeed 方法：
	        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
	        secureRandom.setSeed(keyStr.getBytes("UTF-8"));
	        generator.init(secureRandom);
	        key = generator.generateKey();
			
//			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
			//加密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.ENCRYPT_MODE, key);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	//keybyte为加密密钥，长度为24字节
	//src为加密后的缓冲区
	public static byte[] decryptMode(String keyStr, byte[] src) {
		try {
			//生成密钥
			Key key;
	        KeyGenerator generator = KeyGenerator.getInstance(Algorithm);
	        //generator.init(new SecureRandom("testkey".getBytes(charset)));//linux環境下會每次會不同，windows不會??
	        //SecureRandom 實現嘗試完全隨機化生成器本身的內部狀態，除非調用方在調用 getInstance 方法之後又調用了 setSeed 方法：
	        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
	        secureRandom.setSeed(keyStr.getBytes("UTF-8"));
	        generator.init(secureRandom);
	        key = generator.generateKey();
//			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
			//解密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.DECRYPT_MODE, key);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
//			e1.printStackTrace();
//			System.out.println(e1);
			logger.error(e1);
		} catch (javax.crypto.NoSuchPaddingException e2) {
//			e2.printStackTrace();
//			System.out.println(e2);
			logger.error(e2);
		} catch (java.lang.Exception e3) {
//			e3.printStackTrace();
//			System.out.println(e3);
			logger.error(e3);
		}
		return null;
	}

	/**
	 * =====================================================
	 * 為了保證信息的完整性，利用Base64對加密后的字串編碼
	 * @param b
	 * @return
	 */
	public static String DataEncrypt(String str, String keyStr) {
		String encrypt = null;
		try {
			byte[] ret = encryptMode(keyStr,str.getBytes("UTF-8"));
			if(ret != null){
				encrypt = new String(Base64.encode(ret));
			}
		} catch (Exception e) {
			logger.error(e);
			encrypt = str;
		}
		
//		System.out.println("=======  threeDes dataEncrypt data == " + encrypt);
//		System.out.println("=======  threeDes dataEncrypt keyStr == " + keyStr);
		
		return encrypt;
	}

	/**
	 * =====================================================
	 * 利用Base64對加密過的字串解碼后再解密
	 * @param str
	 * @param key
	 * @return
	 */
	public static String DataDecrypt(String str, String keyStr) {
//		System.out.println("===============  threeDes dataDecrypt data == " + str);
//		System.out.println("===============  threeDes dataDecrypt keyStr == " + keyStr);
		String decrypt = null;
		try {
			byte[] ret = decryptMode(keyStr, Base64.decode(str));
			if(ret != null ){
				decrypt = new String(ret, "UTF-8");
			}
			return decrypt;
		} catch (Exception e) {
			logger.error(e);
			return decrypt;
		}
		
	}

	//二進制转换成十六进制字符串
	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
			if (n < b.length - 1)
				hs = hs + ":";
		}
		return hs.toUpperCase();
	}

	//十六进制转换成二进制字符串
	public static byte[] hex2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	

	//	public static void main(String[] args){
	//
	//	// 添加新安全算法,如果用JCE就要把它添加进去
	//	Security.addProvider(new com.sun.crypto.provider.SunJCE());
	//	final byte[] keyBytes = {0x11, 0x22, 0x4F, 0x58,
	//	(byte)0x88, 0x10, 0x40, 0x38, 0x28, 0x25, 0x79, 0x51,
	//	(byte)0xCB, (byte)0xDD, 0x55, 0x66, 0x77, 0x29, 0x74,
	//	(byte)0x98, 0x30, 0x40, 0x36, (byte)0xE2
	//	}; //24字节的密钥
	//
	//	String szSrc = "This is a 3DES test. 测试";
	//	System.out.println(" 加密前的字符串:" + szSrc);
	//
	//	byte[] encoded = encryptMode(keyBytes, szSrc.getBytes());
	//	System.out.println("加密后的字符串:" + new String(encoded));
	//
	//	byte[] srcBytes = decryptMode(keyBytes, encoded);
	//	System.out.println("解密后的字符串:" + (new String(srcBytes)));
	//	}
}
