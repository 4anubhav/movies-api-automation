package com.airteltv.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserCoreUtils {

    private static final Logger logger            = LoggerFactory.getLogger(UserCoreUtils.class.getCanonicalName());
    private static final int    MSN_UUV0_SIZE_MAX = 17;
    private static final int    MSN_UUV1_SIZE_MAX = 27;
    private static       String key               = "81BHyAUfMgCiu9I7XqArF1Bvy0o";
    private static final String EMAIL_REGEX       = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

    private static String hmacSha1Enc(String key, String in, int max) throws Exception {
        if(max > 0) {
            String Base64Data = base64(hmacsha1(key, in));
            String Base64urlencodeData = Base64Data.replace("+", "-").replace("/", "_").replace("=", "");

            if(Base64urlencodeData.length() > max) {
                return Base64urlencodeData.substring(0, max);
            }
            return Base64urlencodeData;
        }

        return "";
    }

    private static byte[] hmacsha1(String key, String in) throws Exception {
        try {
            SecretKeySpec sk = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(sk);
            return mac.doFinal(in.getBytes());
        }
        catch (Exception e) {
            throw e;
        }
    }

    private static String base64(byte[] hmacsha1_data) throws Exception {
        String Base64EncodeData = new String(Base64.encodeBase64(hmacsha1_data));

        return Base64EncodeData;
    }

    public static String getUidFromMsisdn(String msisdn) {
        return generateUUID(msisdn, null);
    }

    public static String generateUUID(String msisdn, String deviceId) {
        String uuid = null;
        try {
            if(StringUtils.isNotBlank(msisdn)) {
                msisdn = NumberUtils.normalizePhoneNumber(msisdn);
                uuid = String.valueOf(hmacSha1Enc(key, msisdn, MSN_UUV0_SIZE_MAX)) + "0";
            }
            else if(StringUtils.isNotBlank(deviceId)) {
                uuid = String.valueOf(hmacSha1Enc(key, deviceId, MSN_UUV1_SIZE_MAX)) + "2";
            }
            else {
                logger.error("Both msisdn and deviceId are blank");
            }
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return uuid;

    }

    public static String generateAppToken(String uid, String secretKey) {
        try {
            return EncryptUtils.calculateRFC2104HMAC(uid, secretKey);

        }
        catch (Exception e) {
            logger.error("Error while generating apptoken ", e);
            return null;
        }
    }

    public static boolean isEmailValid(String email) {
        return StringUtils.isNotBlank(email) && email.matches(EMAIL_REGEX);
    }

    public static Date formatNdsDate(String dateStr) {
        DateFormat df = new SimpleDateFormat("mm-dd-yyyy");
        Date date = null;
        try {
            if(StringUtils.isNotBlank(dateStr))
                date = df.parse(dateStr);
        }
        catch (Exception ex) {
           
        }
        return date;
    }
    
    

}
