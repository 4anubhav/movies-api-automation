package com.airteltv.utility;

import java.security.Key;
import java.security.MessageDigest;
import java.security.SignatureException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptUtils {

	private static final String ALGORITHM = "AES";
	private static final int shiftKey = 3;
	private static final Logger logger = LoggerFactory.getLogger(EncryptUtils.class.getCanonicalName());

	public static String decrypt(String valueToDeenc, String encKey) {
		try {
			Key key = generateKey(encKey.getBytes("utf-8"));

			byte[] content = Base64.decodeBase64(valueToDeenc.getBytes());
			Cipher c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.DECRYPT_MODE, key);
			byte[] data = c.doFinal(content);
			String res = new String(data);
			return res;
		} catch (Throwable e) {
			throw new RuntimeException("Error While decrypt the key", e);
		}
	}

	private static Key generateKey(byte[] keyBytes) throws Exception {
		Key key = new SecretKeySpec(keyBytes, ALGORITHM);
		return key;
	}

	public static String getRotatedString(String data) {
		int fullPhoneLength = data.length();
		String rotatePart = data.substring(fullPhoneLength - shiftKey);
		rotatePart = new StringBuffer(rotatePart).reverse().toString();
		data = rotatePart + data.substring(0, fullPhoneLength - shiftKey);
		return data;
	}

	public static String decodeShiftAlpha(String data) {
		String number = "";
		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			int digit = (c) - ('A');
			number += digit;
		}
		String rotatePart = number.substring(0, shiftKey);
		rotatePart = new StringBuilder(rotatePart).reverse().toString();
		String result = number.substring(shiftKey) + rotatePart;
		return result;
	}

	public static String encrypt(String valueToEnc, String encKey) throws Exception {
		if (StringUtils.isEmpty(valueToEnc)) {
			return valueToEnc;
		}
		Key key = generateKey(encKey.getBytes("utf-8"));
		Cipher c = Cipher.getInstance(ALGORITHM);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encValue = c.doFinal(valueToEnc.getBytes());
		String encryptedValue = Base64.encodeBase64String(encValue);
		return encryptedValue;
	}

	public static String generateMD5Hash(String str) {

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());

			byte byteData[] = md.digest();

			// convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}

			return sb.toString();
		} catch (Exception e) {
			logger.error("Error while digesting string: {}", e.getMessage(), e);
		}
		return null;

	}

	public static String calculateRFC2104HMAC(String data, String secretKey) throws java.security.SignatureException {
		String result;
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");
			mac.init(key);
			byte[] authentication = mac.doFinal(data.getBytes());
			result = new String(org.apache.commons.codec.binary.Base64.encodeBase64(authentication));

		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		return result;
	}

    public static String calculateHMACSHA256Base64(String data, String secretKey) throws java.security.SignatureException {
		String result;
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
			mac.init(key);
			byte[] authentication = mac.doFinal(data.getBytes());
            result = new String(org.apache.commons.codec.binary.Base64.encodeBase64(authentication));
		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		return result;
	}

    public static String calculateHMACSHA256Hex(String data, String secretKey) {
        String result = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            mac.init(key);
            byte[] authentication = mac.doFinal(data.getBytes());
            result = new String(org.apache.commons.codec.binary.Hex.encodeHexString(authentication));
        }
        catch (Exception e) {
            logger.error("Error while creating hamcsha256 Hex-string: {}", e.getMessage(), e);
        }
        return result;
    }

	/**
	 * Create an MD5 hash of a string.
	 *
	 * @param input
	 *            Input string.
	 * @return Hash of input.
	 * @throws IllegalArgumentException
	 *             if {@code input} is blank.
	 */
	public static String md5(String input) {
		if (input == null || input.length() == 0) {
			throw new IllegalArgumentException("Input string must not be blank.");
		}
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(input.getBytes());
			byte[] messageDigest = algorithm.digest();

			StringBuilder hexString = new StringBuilder();
			for (int i = 0; i < messageDigest.length; i++) {
				hexString.append(Integer.toHexString((messageDigest[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return hexString.toString();
		} catch (Exception e) {
			throw new RuntimeException(String.format("Cannot generate md5 for string [%s]", input));
		}
	}

	public static String encodeShiftAlpha(String data) {
		data = getRotatedString(data);
		String resultString = "";
		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			if (c >= '0' && c <= '9') {
				int digit = Character.getNumericValue(c);
				char dig = (char) (('A') + digit);
				resultString += dig;
			}
		}
		return resultString;
	}
}
