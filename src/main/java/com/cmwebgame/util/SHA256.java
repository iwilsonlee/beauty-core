package com.cmwebgame.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.cmwebgame.exceptions.PortalException;

/**
 * Encodes a string using SHA-256 hashing 
 * 
 */
public class SHA256 {

	/**
	 * Encodes a string by SHA-256
	 * 
	 * @param str String to encode
	 * @return Encoded String
	 */
	public static String encrypt(String str) {
		if (str == null || str.length() == 0) {
			throw new IllegalArgumentException("String to encript cannot be null or zero length");
		}

		StringBuffer hexString = new StringBuffer();

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(str.getBytes());
			byte[] hash = md.digest();

			for (int i = 0; i < hash.length; i++) {
				if ((0xff & hash[i]) < 0x10) {
					hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
				} else {
					hexString.append(Integer.toHexString(0xFF & hash[i]));
				}
			}
		} catch (NoSuchAlgorithmException e) {
			throw new PortalException("" + e);
		}

		return hexString.toString();
	}

}
