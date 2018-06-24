package com.airteltv.utility;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.HttpHost;
import org.zeroturnaround.zip.ZipUtil;

import com.airteltv.reports.Log;
import com.airteltv.reports.LoggerWrapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class Utils {

	public static String BE_BASE_URL = "";//Constants.BE_BASE_URL;
	private static final String ALGORITHM = "AES";
	private LoggerWrapper loggerWrapper = LoggerWrapper.getInstance();
	
	/*public static HttpHeaders getHeader_AUTH_HEADER_BE(String _msisdn) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("AUTH_HEADER", _msisdn);
		return headers;
	}

	public static HttpHeaders getHeader_AUTH_HEADER_WCF(String _msisdn) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("AUTH_HEADER", Auth.md5(_msisdn));
		return headers;
	}*/


	public static boolean isConnected() {
		try {
			URL url = new URL("http://www.google.com");
			url.openConnection().setReadTimeout(1000);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isSubSet(String[] Sub, String[] Super) {
		if (compareContent(Sub, Super) == 1 || compareContent(Sub, Super) == 0)
			return true;
		else
			return false;
	}

	public static boolean isSubSet(String Sub, String Super) {
		return isSubSet(Sub.split(","), Super.split(","));
	}

	public static boolean isSubSet(String Sub, List<String> Super) {
		return isSubSet(Sub.split(","), Super.toArray(new String[Super.size()]));
	}

	public static boolean isSubSet(List<String> Sub, List<String> Super) {
		return isSubSet(Sub.toArray(new String[Sub.size()]), Super.toArray(new String[Super.size()]));
	}

	/**
	 * @param _StringSet1
	 * @param _StringSet2
	 * @return
	 */
	public static int compareContent(String _StringSet1, String _StringSet2) {
		String[] StringSet1 = _StringSet1.split(",");
		String[] StringSet2 = _StringSet2.split(",");
		return compareContent(StringSet1, StringSet2);
	}

	public static int compareContent(List<String> _StringSet1, List<String> _StringSet2) {
		return compareContent(_StringSet1.toArray(new String[_StringSet1.size()]),
				_StringSet2.toArray(new String[_StringSet2.size()]));
	}

	public static int compareContent(String[] _StringSet1, List<String> _StringSet2) {
		return compareContent(_StringSet1, _StringSet2.toArray(new String[_StringSet2.size()]));
	}

	public static int compareContent(List<String> _StringSet1, String[] _StringSet2) {
		return compareContent(_StringSet1.toArray(new String[_StringSet1.size()]), _StringSet2);
	}

	public static int compareContent(String[] _StringSet1, String[] _StringSet2) {
		int len_set1 = _StringSet1.length;
		int len_set2 = _StringSet2.length;
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < len_set1; i++) {
			map.put(_StringSet1[i], -1);
		}
		for (int i = 0; i < len_set2; i++) {
			if (map.containsKey(_StringSet2[i]))
				map.put(_StringSet2[i], 0);
			else
				map.put(_StringSet2[i], 1);
		}
		int returnParam = 0;
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			if (entry.getValue() == -1) {
				if (returnParam == 0)
					returnParam = -1;
				if (returnParam == 1) {
					returnParam = 2;
					break;
				}
			}
			if (entry.getValue() == 1) {
				if (returnParam == 0)
					returnParam = 1;
				if (returnParam == -1) {
					returnParam = 2;
					break;
				}
			}
		}
		return returnParam;
	}


	
	public static String pickNRandom(String[] array, int n) {
		List<String> list = new ArrayList<String>(array.length);
		for (String i : array)
			list.add(i);
		Collections.shuffle(list);

		String answer = list.get(0);
		for (int i = 1; i < n; i++) {
			answer = answer + "," + list.get(i);
		}
		return answer;
	}

	public static String getRandomLangParams(int totalLangs) {
		String returnParam = pickNRandom("hi,en,pa,bj,te,ta,mr,ml,ba,gu,ra,or,as,kn".split(","), totalLangs);
		Log.info("Returning language param - " + returnParam);
		return returnParam;
	}

	public static String toDate(String millis) {
		return toDate(Long.parseLong(millis));
	}

	public static String toDate(long millis) {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis);
		final String timeString = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(cal.getTime());
		return timeString;
	}

	@SuppressWarnings("unchecked")
	private static void collectAllTheKeys(List<String> keys, Object o) {
		Collection<Object> values = null;
		if (o instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) o;
			keys.addAll(map.keySet()); // collect keys at current level in
										// hierarchy
			values = map.values();
		} else if (o instanceof Collection) {
			values = (Collection<Object>) o;
		} else {
			return;
		}
		for (Object value : values) {
			collectAllTheKeys(keys, value);
		}
	}

	public static List<String> getKeysFromJson(String jsoString) {
		Object things = new Gson().fromJson(jsoString, Object.class);
		List<String> keys = new ArrayList<String>();
		collectAllTheKeys(keys, things);
		return keys;
	}

	public static int[] jsonArrayToArray(JsonArray array) {
		int[] objArray = new int[array.size()];
		for (int i = 0; i < array.size(); i++) {
			objArray[i] = Integer.parseInt(array.get(i).toString());
		}
		return objArray;
	}

	public static String toDate(String millis, String format) {
		return toDate(Long.parseLong(millis), format);
	}

	public static String toDate(long millis, String format) {
		Date date=new Date(millis);
		SimpleDateFormat sdfDestination = new SimpleDateFormat(format, Locale.ENGLISH);
		final String timeString = sdfDestination.format(date);
		// final String timeString = new SimpleDateFormat(format).format(cal.getTime());
		return timeString;
	}

	public static String formatDateToString(String inputDate, String format, String timeZone) throws ParseException {
		SimpleDateFormat sdfSource = new SimpleDateFormat(format);

		// parse the string into Date object
		Date date = sdfSource.parse(inputDate);

		// create SimpleDateFormat object with desired date format
		SimpleDateFormat sdfDestination = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss", Locale.ENGLISH);

		// parse the date into another format
		inputDate = sdfDestination.format(date);
		return inputDate;
	}

	public static void convertToZip(String filePath, String outputPath) {
		String date = getTodaysDate();
		ZipUtil.pack(new File(filePath), new File(outputPath));

	}

	public static void uploadReportsAndSendMail() throws IOException, InterruptedException, MessagingException {
		MailSenderUtility mail = new MailSenderUtility();
		String outputZipPath = "/Users/b0201958/Documents/AutomationProjects/music_backend_qa/wynk-music/test-output/reports-be-"
				+ Utils.getTodaysDate() + ".zip";
		convertToZip("/Users/b0201958/Documents/AutomationProjects/music_backend_qa/wynk-music/test-output/html",
				outputZipPath);
		Log.info("Converted file to zip, now uploading it on server 142");
		Log.info("scp " + outputZipPath + " disha@10.1.2.142:/data/tmp");
		ShellUtils.executeShellFromLocal("scp " + outputZipPath + " disha@10.1.2.142:/data/tmp");
		ShellUtils.executeCommand("10.1.2.142", "sh +x /data/tmp/report.sh BE");
		mail.sendMail("Wynk Backend API Automation Report");

	}

	public static String getU(String msisdnOrUid) {
		String u;
		if (msisdnOrUid == null || msisdnOrUid.length() == 0) {
			return null;
		} else {
			u = getUFromUid(getUID(msisdnOrUid));
		}

		Log.info("U - " + u);
		return u;
	}

	public static String getTodaysDate() {
		return Utils.toDate(System.currentTimeMillis(), "YYYY-MM-dd");
	}

	public static String getUID(String msisdnOrUid) {
		if (msisdnOrUid.length() == 18 || msisdnOrUid.length() == 28) {
			return msisdnOrUid;
		} else {
			return getUidMsisdn(msisdnOrUid);
		}
	}

	public static String getUidMsisdn(String phoneNumber) {
		String msisdn = getMSISDNFormatted(phoneNumber);
		String uuid;
		try {
			uuid = String.valueOf(hmacSha1Enc("81BHyAUfMgCiu9I7XqArF1Bvy0o", msisdn, 17)) + "0";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return uuid;
	}

	public static String getUidDevice(String deviceId) {
		String uuid;
		try {
			uuid = String.valueOf(hmacSha1Enc("81BHyAUfMgCiu9I7XqArF1Bvy0o", deviceId, 28)) + "2";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return uuid;
	}

	public static String getUFromUid(String uid) {
		String encryptAndEncodeParam = encryptAndEncodeParam("BSB$PORTAL@2014#", uid);
		return encryptAndEncodeParam;
	}

	@SuppressWarnings("deprecation")
	public static String encryptAndEncodeParam(String encryptionKey, String param) {
		if (StringUtils.isBlank(param)) {
			return StringUtils.EMPTY;
		}
		try {
			param = encrypt(param, encryptionKey);
			param = URLEncoder.encode(param);
		} catch (Exception e) {
			System.out.println("error");
		}
		return param;
	}

	public static String encrypt(String valueToEnc, String encKey) throws Exception {
		if (StringUtils.isEmpty(valueToEnc))
			return valueToEnc;
		Key key = generateKey(encKey.getBytes("utf-8"));
		Cipher c = Cipher.getInstance(ALGORITHM);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encValue = c.doFinal(valueToEnc.getBytes());
		String encryptedValue = Base64.encodeBase64String(encValue);
		return encryptedValue;
	}

	private static byte[] hmacsha1(String key, String in) {
		try {
			SecretKeySpec sk = new SecretKeySpec(key.getBytes(), "HmacSHA1");
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(sk);
			return mac.doFinal(in.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String hmacSha1Enc(String key, String in, int max) {
		if (max > 0) {
			String Base64Data = base64(hmacsha1(key, in));
			String Base64urlencodeData = Base64Data.replace("+", "-").replace("/", "_").replace("=", "");

			if (Base64urlencodeData.length() > max) {
				return Base64urlencodeData.substring(0, max);
			}
			return Base64urlencodeData;
		}

		return "";
	}

	private static String base64(byte[] hmacsha1_data) {
		String Base64EncodeData = new String(Base64.encodeBase64(hmacsha1_data));
		return Base64EncodeData;
	}

	private static Key generateKey(byte[] keyBytes) {
		Key key = new SecretKeySpec(keyBytes, ALGORITHM);
		return key;
	}

	public static String getMSISDNFormatted(String phoneNumber) {
		String msisdn = null;
		if (phoneNumber != null && phoneNumber.length() >= 10) {
			msisdn = (phoneNumber.length() == 10) ? phoneNumber
					: phoneNumber.substring(phoneNumber.length() - 10, phoneNumber.length());
			msisdn = "+91" + msisdn;
			return msisdn;
		} else {
			return null;
		}
	}

	private static long currentTime = 0;

	public static long getCurrentTime() {
		if (currentTime == 0)
			currentTime = System.currentTimeMillis();
		return currentTime;
	}

	public static boolean isValidUrl(String url) {
		String[] schemes = { "http", "https", "ftp" };
		UrlValidator urlValidator = new UrlValidator(schemes);
		return urlValidator.isValid(url);
	}

	public static String toPrettyJson(String uglyJSONString) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(uglyJSONString);
		String prettyJsonString = gson.toJson(je);
		return prettyJsonString;
	}

	public static String toJsonString(Object obj) {
		return new Gson().toJson(obj);
	}

	public static String toPrettyJson(Object obj) {
		return toPrettyJson(toJsonString(obj));
	}

	public static void printPrettyJson(Object obj) {
		Log.info("\n" + toPrettyJson(obj));
	}

	public static int getRandomInt(int min, int max) {
		return (min + (int) (Math.random() * ((max - min) + 1)));
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jsonToMap(String json) {
		Gson gson = new Gson();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = gson.fromJson(json, map.getClass());
		} catch (Exception e) {
			return null;
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jsonToMap(Object jsonObj) {
		String json = new Gson().toJson(jsonObj);
		Gson gson = new Gson();
		Map<String, Object> map = new HashMap<String, Object>();
		map = gson.fromJson(json, map.getClass());
		return map;
	}

	public static boolean matchPattern(String pattern, String _input) {
		String input = _input;
		if (StringUtils.isBlank(_input) || StringUtils.isBlank(pattern))
			return true;
		Log.info("input - " + input + " match - " + Pattern.matches(pattern, input) + " replaced "
				+ input.replaceAll(pattern, ""));
		if (Pattern.matches(pattern, input) && input.replaceAll(pattern, "").length() == 0)
			return true;
		return false;
	}

	public static boolean isJsonValid(String json) {
		json = json.trim().replaceAll("\n", "");
		if (!((json.startsWith("{") && json.endsWith("}")) || (json.startsWith("[") && json.endsWith("]")))) {
			return false;
		}
		JsonParser jp = new JsonParser();
		try {
			jp.parse(json);
		} catch (Exception ex) {
			// ex.printStackTrace();
			return false;
		}
		return true;
	}

	public static final HttpHost getHost(String BASE_URL) throws MalformedURLException {
		URL url = new URL(BASE_URL);
		if (url.getPort() > -1)
			return new HttpHost(url.getHost(), url.getPort());
		if (url.getPort() > -1)
			return new HttpHost(url.getHost());
		return new HttpHost(url.getHost());
	}

	public static Double getStringSimilarity(String str1, String str2) {
		str1 = str1.replaceAll("[^0-9a-zA-Z ]", "");
		str2 = str2.replaceAll("[^0-9a-zA-Z ]", "");
		if (StringUtils.isBlank(str1) || StringUtils.isBlank(str2))
			return 0D;
		// Can't compare with empty String

		String smallerString = str1.length() < str2.length() ? str1 : str2;
		String largerString = str1.length() > str2.length() ? str1 : str2;
		String[] smallerArray = smallerString.split("[ ,;.-]");
		int l = smallerArray.length;
		Double fullStringO = getStringSimilarityCoef(str1, str2);
		Double internalO = 0D;
		for (int i = 0; i < l; i++) {
			internalO += getStringSimilarityCoef(smallerArray[i], largerString);
		}
		Double similarityBasedOnCommonSubString = (fullStringO * 2 + internalO) / (l + 2);

		Double factorLevenshteinDistance = (double) ((smallerString.length() - getLevenshteinDistance(str1, str2)) * 100
				/ smallerString.length());
		return similarityBasedOnCommonSubString > factorLevenshteinDistance ? similarityBasedOnCommonSubString
				: factorLevenshteinDistance;
	}

	public static String getLargestCommonSubstring(String str1, String str2) {
		StringBuilder sb = new StringBuilder();
		if (str1 == null || str1.isEmpty() || str2 == null || str2.isEmpty())
			return "";

		// ignore case4
		str1 = str1.toLowerCase();
		str2 = str2.toLowerCase();

		// java initializes them already with 0
		int[][] num = new int[str1.length()][str2.length()];
		int maxlen = 0;
		int lastSubsBegin = 0;

		for (int i = 0; i < str1.length(); i++) {
			for (int j = 0; j < str2.length(); j++) {
				if (str1.charAt(i) == str2.charAt(j)) {
					if ((i == 0) || (j == 0))
						num[i][j] = 1;
					else
						num[i][j] = 1 + num[i - 1][j - 1];

					if (num[i][j] > maxlen) {
						maxlen = num[i][j];
						// generate substring from str1 => i
						int thisSubsBegin = i - num[i][j] + 1;
						if (lastSubsBegin == thisSubsBegin) {
							// if the current LCS is the same as the last time this block ran
							sb.append(str1.charAt(i));
						} else {
							// this block resets the string builder if a different LCS is found
							lastSubsBegin = thisSubsBegin;
							sb = new StringBuilder();
							sb.append(str1.substring(lastSubsBegin, i + 1));
						}
					}
				}
			}
		}

		return sb.toString();
	}

	private static Double getStringSimilarityCoef(String str1, String str2) {
		str1 = str1.replaceAll("[^0-9a-zA-Z]", "");
		str2 = str2.replaceAll("[^0-9a-zA-Z]", "");
		if (str1 == null || str1.isEmpty() || str2 == null || str2.isEmpty())
			return 0D;
		String sb = getLargestCommonSubstring(str1, str2);

		String smlString = str1;
		if (str2.length() < smlString.length())
			smlString = str2;

		int sl = smlString.length();
		Double factorCommonStrings = (sb.length() * 100.0 / sl);

		return factorCommonStrings;
	}

	private static int getLevenshteinDistance(String s, String t) {
		if (s == null || t == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}
		int n = s.length(); // length of s
		int m = t.length(); // length of t

		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}

		if (n > m) {
			// swap the input strings to consume less memory
			String tmp = s;
			s = t;
			t = tmp;
			n = m;
			m = t.length();
		}

		int p[] = new int[n + 1]; // 'previous' cost array, horizontally
		int d[] = new int[n + 1]; // cost array, horizontally
		int _d[]; // placeholder to assist in swapping p and d

		// indexes into strings s and t
		int i; // iterates through s
		int j; // iterates through t

		char t_j;
		int cost;

		for (i = 0; i <= n; i++) {
			p[i] = i;
		}

		for (j = 1; j <= m; j++) {
			t_j = t.charAt(j - 1);
			d[0] = j;

			for (i = 1; i <= n; i++) {
				cost = s.charAt(i - 1) == t_j ? 0 : 1;
				d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
			}

			_d = p;
			p = d;
			d = _d;
		}
		return p[n];
	}

	public static String right(String s, int l) {
		int len = s.length();
		int ind = len - l;
		ind = ind < 0 ? 0 : ind;
		return s.substring(ind);
	}

	public static String left(String s, int l) {
		int len = s.length();
		int ind = l >= len ? len : l;
		return s.substring(0, ind);
	}
	/* --- to add multiple params in Map ---- */
	public static Map<String, Object> getMap(Object... args) {
		Map<String, Object> returnParam = new LinkedHashMap<String, Object>();
		int argsLen = args.length;
		for (int i = 0; i < argsLen; i = i + 2) {
			if (i == argsLen - 1)
				returnParam.put(args[i] + "", null);
			else
				returnParam.put(args[i] + "", args[i + 1]);
		}
		return returnParam;
	}
	
	/* json path value using jayway */
	public String extractJSONPath(String jsonString, String jsonPath) throws Exception {
		Object jsonPathResult = JsonPath.read(jsonString, jsonPath);
		if (null == jsonPathResult) {
			throw new Exception("Invalid JSON path provided!");
		} else
			try {
				if (jsonPathResult instanceof List && ((List<?>) jsonPathResult).isEmpty()) {
					return "NULL";
				} else {
					return jsonPathResult.toString();
				}
			} catch (PathNotFoundException e) {
				String stacktrace = ExceptionUtils.getStackTrace(e);
				loggerWrapper.myLogger.error(jsonPath + " is invalid | " + stacktrace);
				return null;
			}
	}
	
	/* json path value using jayway as List<String> */
	public List<String> extractJSONPathValueAsList(String jsonString, String jsonPath) throws Exception {
		List<String> jsonPathResult = JsonPath.read(jsonString, jsonPath);
		if (null == jsonPathResult) {
			throw new Exception("Invalid JSON path provided!");
		} else
			try {
				if (jsonPathResult instanceof List && ((List<?>) jsonPathResult).isEmpty()) {
					return null;
				} else {
					return jsonPathResult;
				}
			} catch (PathNotFoundException e) {
				String stacktrace = ExceptionUtils.getStackTrace(e);
				loggerWrapper.myLogger.error(jsonPath + " is invalid | " + stacktrace);
				return null;
			}
	}
}