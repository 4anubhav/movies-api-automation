package com.airteltv.utility;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class NumberUtils {

    private static final Logger logger            = LoggerFactory.getLogger(NumberUtils.class);
    private static final int    MSN_UUV0_SIZE_MAX = 17;
    private static String       key               = "81BHyAUfMgCiu9I7XqArF1Bvy0o";
    private static final String AB                = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd               = new SecureRandom();

    public static int getPageInation(int startIndex, int total, int numPerPage) {
        if(startIndex < 0 || total < 0 || numPerPage < 0) {
            return 0;
        }

        if((total - startIndex) < 0) {
            return 0;
        }

        if(total < numPerPage)
            return total;

        else {
            //count of items this page which is minimum of number per page or items left
            return (numPerPage <= (total - startIndex))? numPerPage : total - startIndex;
        }
    }

    public static int getRandomNumber(int maxValue) {
        int numb = 1 + (int) (maxValue * Math.random());
        if(numb > 0 && numb <= maxValue) {
            return numb;
        }
        return 0;
    }

    public static String getRandomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public static int getRandomNumberInRange(int min, int maxValue) {
        Random rnd = new Random();
        return min + rnd.nextInt(90);
    }

    public static String getOrdinal(int number) {
        if(number % 10 == 1) {
            return number + "st";
        }
        else if(number % 10 == 2) {
            return number + "nd";
        }
        else if(number % 10 == 3) {
            return number + "rd";
        }
        else {
            return number + "th";
        }
    }

    public static String formatNumber(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("###.#");
        return decimalFormat.format(value);
    }

    /**
     * Get 10 digit msisdn
     *
     * @param msisdn
     * @return
     */
    public static String get10DigitMsisdn(String msisdn) {
        if(StringUtils.isNotEmpty(msisdn) && msisdn.length() > 10) {
            return msisdn.substring(msisdn.length() - 10, msisdn.length());
        }
        return msisdn;

    }

    public static String normalizePhoneNumber(String ph) {
        if(ph == null || ph.isEmpty() || (ph.startsWith("+") && ph.length() == 13 && !containsAlpha(ph))) {
            return ph;
        }

        try {
            if(containsAlpha(ph)) {
                return null;
            }
            else if(ph.startsWith("91") && ph.length() == 12) {
                return "+" + ph;
            }
            else if(ph.length() == 11 && ph.startsWith("0")) // phone number starts with 0 e.g.
            // 09811920234
            {
                return "+91" + ph.substring(1, ph.length());
            }
            else if(ph.length() == 14 && ph.startsWith("0091")) // phone number starts with 0 e.g.
            // 00918527401222
            {
                return "+" + ph.substring(2, ph.length());
            }
            else if(ph.length() == 10) {
                Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(ph, "IN");
                return PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            }
            else {
                return null;
            }
        }
        catch (NumberParseException e) {
            // this can also be the case if we use number like "TD-HIKE"
            // fallback to the raw parsing
            if(ph.startsWith("91") && ph.length() == 12) {
                return "+" + ph;
            }
            else if(ph.length() == 10) {
                return "+91" + ph;
            }
            else if(ph.length() == 11 && ph.startsWith("0")) // phone number starts with 0 e.g.
            // 09811920234
            {
                return "+91" + ph.substring(1, ph.length());
            }
            else if(ph.length() == 14 && ph.startsWith("0091")) // phone number starts with 0 e.g.
            // 00918527401222
            {
                return "+" + ph.substring(2, ph.length());
            }
            return null;
        }

    }

    public static boolean containsAlpha(String str) {
        if(str == null) {
            return false;
        }
        for(int i = str.length() - 1; i >= 0; i--) {
            char c = str.charAt(i);
            if(Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }

    public static String generateUUID(String msisdn) {
        String uuid = null;
        try {
            if(StringUtils.isNotBlank(msisdn)) {
                msisdn = normalizePhoneNumber(msisdn);
                uuid = String.valueOf(hmacSha1Enc(key, msisdn, MSN_UUV0_SIZE_MAX)) + "0";
            }
            else {
                // TODO case for we
                logger.error("Both msisdn and deviceId are blank");
            }
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return uuid;

    }

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

    /**
     * return 10 digit msisdn. throws IllegalArgumentException if msisdn passed is less than 10
     * characters. It does not validate msisdn for being numeric.
     * 
     * @param msisdn
     * @return 10 digit msisdn
     */
    public static String getTenDigitMsisdn(String msisdn) {
        if(StringUtils.isEmpty(msisdn)) {
            return msisdn;
        }
        msisdn = msisdn.trim();
        int length = msisdn.length();
        if(length == 10) {
            return msisdn;
        }
        if(length > 10) {
            return msisdn.substring(length - 10);
        }
        throw new IllegalArgumentException("Illegal value for msisdn : " + msisdn);
    }

    /**
     * Get 12 digit msisdn
     *
     * @param msisdn
     * @return
     */
    public static String get12DigitMsisdn(String msisdn) {
        if(StringUtils.isNotEmpty(msisdn)) {
            if(msisdn.length() == 10) {
                return "91" + msisdn;
            }
            else if(msisdn.startsWith("+") && msisdn.length() == 13) {
                return msisdn.substring(1);
            }
        }
        return msisdn;
    }

    public static int getRandomNumber(long maxValue) {
        int numb = 1 + (int) (new Long(maxValue).intValue() * Math.random());
        if(numb > 0 && numb <= maxValue) {
            return numb;
        }
        return 0;
    }

    public static int getRandomNumber(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public static int getRandomNumber(int min, long max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((new Long(max).intValue() - min) + 1) + min;
        return randomNum;
    }

    public static Date getDate(String dateStr, String dateFormat) {
        Date d = null;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            d = sdf.parse(dateStr);
            return d;
        }
        catch (ParseException e) {
            logger.error("Format [{}] not valid to parse [{}]", dateFormat, dateStr);
        }
        return d;
    }

    public static String maskMsisdn(String msisdn) {
        if(StringUtils.isBlank(msisdn)) {
            return StringUtils.EMPTY;
        }
        String normalizePhoneNumber = NumberUtils.normalizePhoneNumber(msisdn);
        return maskNumber(normalizePhoneNumber, "####xxxx####");
    }

    //System.out.println(maskNumber("1234123412341234", "xxxx-xxxx-xxxx-####"));
    // xxxx-xxxx-xxxx-1234
    public static String maskNumber(String number, String mask) {
        // format the number
        int index = 0;
        StringBuilder maskedNumber = new StringBuilder();
        for(int i = 0; i < mask.length(); i++) {
            char c = mask.charAt(i);
            if(c == '#') {
                maskedNumber.append(number.charAt(index));
                index++;
            }
            else if(c == 'x') {
                maskedNumber.append(c);
                index++;
            }
            else {
                maskedNumber.append(c);
            }
        }
        return maskedNumber.toString();
    }

}
