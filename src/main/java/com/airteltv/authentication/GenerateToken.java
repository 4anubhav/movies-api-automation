package com.airteltv.authentication;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.airteltv.utility.EncryptUtils;
import com.airteltv.utility.EnvProperties;
import com.airteltv.utility.UserCoreUtils;

public class GenerateToken {

	public static String generateSignatureGET(String url1, String uid) {
		final String secret = EnvProperties.getEnvProperty("config_", "MW_SECRET_KEY");
		String token;
		String signature = null;
		try {
		token = calculateRFC2104HMAC(uid, secret);
		URL uri = new URL(URLDecoder.decode(url1, "UTF-8"));
		signature = generateHMACSignature("GET", uri.getPath()  + "?" + uri.getQuery(), token);
		} catch (SignatureException | MalformedURLException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return getSignatureInFormat(uid, signature);
	}

	public static String generateSignaturePOST(String url1, String payload, String uid) {
		final String secret = "2d7db9";
				//"blabla";
		String signature = null;
		try {
		String token = calculateRFC2104HMAC(uid, secret);
		URL uri = new URL(URLDecoder.decode(url1, "UTF-8"));
		signature = generateHMACSignature("POST", uri.getPath(), payload,
				token);
		} catch (SignatureException | MalformedURLException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return getSignatureInFormat(uid, signature);
	}

	public static String generateHMACSignature(String httpVerb, String requestUri, String secret)
			throws SignatureException {
		String signature = StringUtils.EMPTY;
		String digestString = new StringBuilder(httpVerb).append(requestUri).toString();
		try {
			signature = EncryptUtils.calculateRFC2104HMAC(digestString, secret);
		} catch (SignatureException e) {
			throw e;
		}
		return signature;
	}

	public static String generateHMACSignature(String httpVerb, String requestUri, String payload,
			 String secret) throws SignatureException {
		String signature = StringUtils.EMPTY;
		String digestString = new StringBuilder(httpVerb).append(requestUri).append(payload).toString();
		// System.out.println(digestString);
		try {
			signature = EncryptUtils.calculateRFC2104HMAC(digestString, secret);
		} catch (SignatureException e) {
			throw e;
		}
		return signature;
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

	public static String getUID(String msisdn) {
		return UserCoreUtils.generateUUID(msisdn, "9193056e7a3cf272|Phone|Android|23|12518|1.9.4");
	}

	public static String getSignatureInFormat(String uid, String signature) {
		return uid + ":" + signature;
	}
	
	public static void main(String[] args) {
	
		String uid = getUID("7838710658");
		String sign = null;
		sign = generateSignatureGET("https://play.airtel.tv/v2/user/content/playback?contentId=ALTBALAJI_EPISODE_1055&appId=MOBILITY", uid);
		
		System.out.println(sign);
	}
	
	
	
}
