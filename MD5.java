package com.eyespage.utils;

import java.security.MessageDigest;

/**
 * @author JeromeSong<br>
 *         MD5加密算法--单向加密用于验证，常与Base64一起使用
 */
public class MD5 {
	public static String encrypt(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			hexValue.append(String.format("%02x", ((int) md5Bytes[i]) & 0xff));
		}
		return hexValue.toString();
	}
}