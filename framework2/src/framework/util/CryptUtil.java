/*
 * @(#)CryptoUtil.java
 */
package framework.util;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 * 암/복호화 관련 기능을 하는 유틸리티 클래스이다.
 */
public class CryptUtil {

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private CryptUtil() {
	}

	/**
	 * 메시지를 MD5 알고리즘으로 해쉬한다.
	 * 
	 * @param message 원본메시지
	 * @return 해쉬된 문자열
	 */
	public static String hashMD5(String message) {
		return hash(message, "MD5");
	}

	/**
	 * 메시지를 SHA-1 알고리즘으로 해쉬한다.
	 * 
	 * @param message 원본메시지
	 * @return 해쉬된 문자열
	 */
	public static String hashSHA1(String message) {
		return hash(message, "SHA-1");
	}

	/**
	 * 메시지를 SHA-256 알고리즘으로 해쉬한다.
	 * 
	 * @param message 원본메시지
	 * @return 해쉬된 문자열
	 */
	public static String hashSHA256(String message) {
		return hash(message, "SHA-256");
	}

	/**
	 * 메시지를 SHA-512 알고리즘으로 해쉬한다.
	 * 
	 * @param message 원본메시지
	 * @return 해쉬된 문자열
	 */
	public static String hashSHA512(String message) {
		return hash(message, "SHA-512");
	}

	/**
	 * 메시지를 BASE64 알고리즘으로 인코딩한다.
	 * 
	 * @param message 원본메시지
	 * @return 인코딩된 문자열
	 */
	public static String encodeBase64(String message) {
		return new String(Base64.encodeBase64(message.getBytes()));
	}

	/**
	 * 메시지를 BASE64 알고리즘으로 디코딩한다.
	 * 
	 * @param message 원본 메시지
	 * @return 디코딩된 문자열
	 */
	public static String decodeBase64(String message) {
		return new String(Base64.decodeBase64(message.getBytes()));
	}

	/**
	 * 메시지를 개인키를 이용하여 AES 알고리즘으로 암호화한다.
	 * 
	 * @param message 원본메시지
	 * @param privateKey 개인키 
	 * @return 암호화된 문자열
	 */
	public static String encryptAES(String message, String privateKey) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(privateKey.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			return String.valueOf(Hex.encodeHex(cipher.doFinal(message.getBytes()))).toUpperCase();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 메시지를 개인키를 이용하여 AES 알고리즘으로 복호화한다.
	 *  
	 * @param message 원본메시지
	 * @param privateKey 개인키 
	 * @return 복호화된 문자열
	 */
	public static String decryptAES(String message, String privateKey) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(privateKey.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			return new String(cipher.doFinal(Hex.decodeHex(message.toCharArray())));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 메시지를 개인키를 이용하여 DES 알고리즘으로 암호화한다.
	 * 
	 * @param message 원본메시지
	 * @param privateKey 개인키 
	 * @return 암호화된 문자열
	 */
	public static String encryptDES(String message, String privateKey) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(privateKey.getBytes(), "DES");
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			return String.valueOf(Hex.encodeHex(cipher.doFinal(message.getBytes()))).toUpperCase();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 메시지를 개인키를 이용하여 DES 알고리즘으로 복호화한다.
	 *  
	 * @param message 원본메시지
	 * @param privateKey 개인키 
	 * @return 복호화된 문자열
	 */
	public static String decryptDES(String message, String privateKey) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(privateKey.getBytes(), "DES");
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			return new String(cipher.doFinal(Hex.decodeHex(message.toCharArray())));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private 메소드

	/**
	 * 메시지를 주어진 알고리즘으로 해쉬한다.
	 * @param message 원본메시지
	 * @param algorithm 해쉬 알고리즘
	 * @return 해쉬된 문자열
	 */
	private static String hash(String message, String algorithm) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			return new String(Hex.encodeHex(md.digest(message.getBytes()))).toUpperCase();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
