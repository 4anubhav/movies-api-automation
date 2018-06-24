package com.airteltv.utility;

import java.util.Arrays;
import java.util.List;

public class Constants {
	
	public static final String secretKey_prod="2d7db9";
	public static final String secretKey_staging="50de5a601c133a29c8db434fa9bf2db4";
	
	public static final Double STRING_MATCH_ACCEPTABLE_VALUE = 75.0;
	public static final Double STRING_MATCH_ACCEPTABLE_VALUE_AUTO_SUGGEST = 75.0;
	public static Constants.OS OS_TYPE = Constants.OS.ANDROID;
	public static final Double STRING_MATCH_MIN_LENGTH = 3D;

	// MSISDNS

	public static final String MSISDN = "9958394935";
	public static final String MSISDN_PREPAID = "9958394935";
	public static final String MSISDN_POSTPAID = "9958394935";
	public static final String NON_AIRTEL_MSISDN = "9999056354";
	public static final String NON_AIRTEL_UID = null;
	public static final String PRODUCT_ID = null;
	public static final String INVALID = null;
	public static final String PREPAID_UID = null;
	public static final String PRODUCT_ID_AIRTEL = null;
	public static final String PRODUCT_ID_NON_AIRTEL = null;
	public static final String POSTPAID_UID = null;
	public static final String PREPAID_MSISDN = null;
	public static final String TEST_PRODUCT_ID = "110004";
	// REGEX
	public static final String REG_ALPHA_NUMERIC = "[^a-zA-Z0-9]";

	// Environment
	private static final boolean ENV_STAGING = true;
	public static final boolean SEARCH_V3 = true;

	public static final String SERVICE = "music";

	// Enums
	public static enum OS {
		ANDROID, IOS, SAMSUNG
	};

	public static enum ContentType {
		SONG, ARTIST, ALBUM, PLAYLIST, PACKAGE
	};

	// FLAGs
	public static final boolean PRINT_NETWORK_LOGS = true;
	public static boolean PRINT_RESPONSE = false;
	public static boolean PRINT_REQUEST_HEADERS = false;
	public static boolean PRINT_RESPONSE_HEADERS = false;

	public static boolean MANDATORY_KEYS = true;
	public static boolean AUTO_SUGGEST = false;
	public static List<String> ignoredMandatoryKeys = Arrays
			.asList("title,subtitle,largeImage,smallImage,originalImage".split(","));
	public static boolean IGNORE_FEW_MANDATORY_KEYS = true;

	// Hosts and Ports
	public final static String CMS_BASE_URL = (ENV_STAGING) ? Staging.CMS_BASE_URL : Production.CMS_BASE_URL;
	public final static String BE_SOLR_URL = (ENV_STAGING) ? Staging.BE_SOLR_URL : Production.BE_SOLR_URL;
	public final static String WCF_BASE_URL = (ENV_STAGING) ? Staging.WCF_BASE_URL : Production.WCF_BASE_URL;
	public final static String BE_BASE_URL = (ENV_STAGING) ? Staging.BE_BASE_URL : Production.BE_BASE_URL;
	public final static String BE_BASE_SEARCH_URL = (ENV_STAGING) ? Staging.BE_BASE_SEARCH_URL
			: Production.BE_BASE_SEARCH_URL;
	public final static String WAP_BASE_URL = (ENV_STAGING) ? Staging.WAP_BASE_URL : Production.WAP_BASE_URL;
	public final static String WCF_REDIS_HOST = (ENV_STAGING) ? Staging.WCF_REDIS_HOST : Production.WCF_REDIS_HOST;
	public final static int WCF_REDIS_PORT = (ENV_STAGING) ? Staging.WCF_REDIS_PORT : Production.WCF_REDIS_PORT;
	public final static String WCF_CASSANDRA_HOST = (ENV_STAGING) ? Staging.WCF_CASSANDRA_HOST
			: Production.WCF_CASSANDRA_HOST;
	public final static int WCF_CASSANDRA_PORT = (ENV_STAGING) ? Staging.WCF_CASSANDRA_PORT
			: Production.WCF_CASSANDRA_PORT;
	public final static String BE_MONGO_HOST = (ENV_STAGING) ? Staging.BE_MONGO_HOST : Production.BE_MONGO_HOST;
	public final static int BE_MONGO_PORT = (ENV_STAGING) ? Staging.BE_MONGO_PORT : Production.BE_MONGO_PORT;
	public final static String BE_REDIS_HOST = (ENV_STAGING) ? Staging.BE_REDIS_HOST : Production.BE_REDIS_HOST;
	public final static int BE_REDIS_PORT = (ENV_STAGING) ? Staging.BE_REDIS_PORT : Production.BE_REDIS_PORT;
	public final static String UT_CASSANDRA_HOST = (ENV_STAGING) ? Staging.UT_CASSANDRA_HOST
			: Production.UT_CASSANDRA_HOST;
	public final static int UT_CASSANDRA_PORT = (ENV_STAGING) ? Staging.UT_CASSANDRA_PORT
			: Production.UT_CASSANDRA_PORT;
	public final static String BE_REDIS_OTP_HOST = (ENV_STAGING) ? Staging.BE_REDIS_OTP_HOST
			: Production.BE_REDIS_OTP_HOST;
	public final static int BE_REDIS_OTP_PORT = (ENV_STAGING) ? Staging.BE_REDIS_OTP_PORT
			: Production.BE_REDIS_OTP_PORT;

	// Auth Params
	public final static String BE_APPID = (ENV_STAGING) ? Staging.BE_APPID : Production.BE_APPID;
	public final static String BE_SECRET = (ENV_STAGING) ? Staging.BE_SECRET : Production.BE_SECRET;
	public final static String WCF_APPID = (ENV_STAGING) ? Staging.WCF_APPID : Production.WCF_APPID;
	public final static String WCF_SECRET = (ENV_STAGING) ? Staging.WCF_SECRET : Production.WCF_SECRET;

	// Device Params
	public final static String X_BSY_DID_SAMSUNG = (ENV_STAGING) ? Staging.X_BSY_DID_SAMSUNG
			: Production.X_BSY_DID_SAMSUNG;
	public final static String X_BSY_DID_ANDROID = (ENV_STAGING) ? Staging.X_BSY_DID_ANDROID
			: Production.X_BSY_DID_ANDROID;
	public final static String X_BSY_DID_IOS = (ENV_STAGING) ? Staging.X_BSY_DID_IOS : Production.X_BSY_DID_IOS;

	// URIs
	public static final String URI_OTP = "/music/v2/account/otp";
	public static final String URI_PROFILE = "/music/v2/account/profile";
	public static final String URI_ACCOUNT = "/music/v2/account";
	public static final String URI_USERTARGETING = "/music/v2/usertargeting";
	public static final String URI_PACKS = "/music/v2/account/packs";
	public static final String URI_CONFIG = "/music/v1/config";
	public static final String URI_CALLBACK_SUBSCRIBE = "/music/wcf/cb/subscribe/tp/callback";
	public static final String URI_STREAM = "/music/v1/cscgw/";
	public static final String URI_RENT = "/music/v1/crcgw/";
	public static final String URI_SEARCH_V2 = "/music/v2/search";
	public static final String URI_SEARCH_V3 = "/music/v3/search";
	public static final String URI_UNISEARCH_V2 = "/music/v2/unisearch";
	public static final String URI_UNISEARCH_V3 = "/music/v3/unisearch";
	public static final String URI_SUBSCRIBE_PACK_V2 = "/music/wcf/v2/subscribePack";
	public static final String URI_CONTENT_V3 = "/music/v3/content";

	public static class Staging {

		public final static String CMS_BASE_URL = "http://10.1.2.31:8080";
		public final static String BE_SOLR_URL = "http://10.0.7.15:8983";
		// public final static String BE_SOLR_URL =
		// "http://http://10.0.7.124:8383/music/v3/unisearch/ltr?q=kismat&lang=en&contentLang=hi";
		public final static String WCF_BASE_URL = "http://dev.wynk.in:8080";
		public final static String BE_BASE_URL = "http://dev.wynk.in:8080";
		// public final static String BE_BASE_SEARCH_URL = "http://10.0.7.124:8383";
		public final static String BE_BASE_SEARCH_URL = "http://dev.wynk.in:8080";
		public final static String WAP_BASE_URL = "http://54.255.185.239:90";
		public final static String WCF_REDIS_HOST = "10.1.2.77";
		public final static int WCF_REDIS_PORT = 6379;
		public final static String UT_CASSANDRA_HOST = "10.1.5.138";
		public final static int UT_CASSANDRA_PORT = 9042;
		public final static String WCF_CASSANDRA_HOST = "10.1.2.77";
		public final static int WCF_CASSANDRA_PORT = 9042;
		public final static String BE_MONGO_HOST = "10.1.1.225";
		public final static int BE_MONGO_PORT = 27017;
		public final static String BE_REDIS_HOST = "10.1.2.222";
		public final static int BE_REDIS_PORT = 6379;
		public final static String BE_REDIS_OTP_HOST = "10.1.2.222";
		public final static int BE_REDIS_OTP_PORT = 6379;
		public final static String BE_APPID = "543fbd6f96644406567079c00d8f33dc";
		public final static String BE_SECRET = "50de5a601c133a29c8db434fa9bf2db4";
		public final static String WCF_APPID = "6915ddabb06cf86324eccd170aa44ea9";
		public final static String WCF_SECRET = "ed02d7dbb8f6923409d230e731f2ff70";

		public final static String X_BSY_DID_SAMSUNG = "2e75c98cf9974f6c/samsung/24/5/1.5.1.1";
		public final static String X_BSY_DID_ANDROID = "2e75c98cf9974f6c/Android/24/100/1.5.1.1";
		public final static String X_BSY_DID_IOS = "B7A22F3A-EF27-4799-9BC5-DB82AB71D5BC/iOS/9.3.2/100/1.6.4";

	}

	public static class Production {

		public final static String CMS_BASE_URL = null;
		public final static String BE_SOLR_URL = "http://10.0.7.233:8080";
		public final static String WCF_BASE_URL = "http://capi.wynk.in";
		public final static String BE_BASE_URL = "http://api.wynk.in";
		public final static String BE_BASE_SEARCH_URL = "http://search.wynk.in";
		public final static String BE_OLD_BASE_SEARCH_URL = "https://content.wynk.in";

		public final static String WAP_BASE_URL = "http://54.255.185.239:90";
		public final static String WCF_REDIS_HOST = null;
		public final static int WCF_REDIS_PORT = 6379;
		public final static String UT_CASSANDRA_HOST = "10.0.4.93";
		public final static int UT_CASSANDRA_PORT = 9042;
		public final static String WCF_CASSANDRA_HOST = "10.40.12.11";
		public final static int WCF_CASSANDRA_PORT = 9042;
		public final static String BE_MONGO_HOST = "10.0.8.105";
		public final static int BE_MONGO_PORT = 27017;
		public final static String BE_REDIS_HOST = null;
		public final static int BE_REDIS_PORT = 6379;
		public final static String BE_REDIS_OTP_HOST = "10.0.7.75";
		public final static int BE_REDIS_OTP_PORT = 6379;
		public final static String BE_APPID = "543fbd6f96644406567079c00d8f33dc";
		public final static String BE_SECRET = "50de5a601c133a29c8db434fa9bf2db4";
		public final static String WCF_APPID = "6915ddabb06cf86324eccd170aa44ea9";
		public final static String WCF_SECRET = "ed02d7dbb8f6923409d230e731f2ff70";

		public final static String X_BSY_DID_SAMSUNG = "2e75c98cf9974f6c/samsung/24/5/1.5.1.1";
		public final static String X_BSY_DID_ANDROID = "2e75c98cf9974f6c/Android/24/69/1.5.1.1";
		public final static String X_BSY_DID_IOS = "B7A22F3A-EF27-4799-9BC5-DB82AB71D5BC/iOS/9.3.2/77/1.6.4";
	}

	public static class ChecksSearch {

		public static boolean CHECK_COUNT_TOTAL = true;
		public static boolean CHECK_CONTENT_TYPE = true;
	}

}
