package com.airteltv.test;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.io.output.WriterOutputStream;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.airteltv.reports.ExtentTestManager;
import com.airteltv.reports.LoggerWrapper;
import com.airteltv.restutils.RestUtil;
import com.airteltv.testdata.ExcelUtil;
import com.airteltv.utility.EnvProperties;
import com.airteltv.utility.Helper;
import com.airteltv.utility.MongoManager;
import com.google.common.base.Splitter;
import com.mongodb.DBObject;

import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
public class PlaybackAPI extends BaseTest {

	private Response res = null; // Response object
	private JsonPath jp = null; // JsonPath object
	RequestSpecification requestSpecification = null;
	Headers headers = null;

	public String contentID;
	public DBObject dbo;
	private static LoggerWrapper loggerWrapper;

	MongoManager mm = MongoManager.connectBEMongo();
	public String os_type;
	public static ArrayList<Object> contentList;
	public int counter = 0;
	public Boolean isDrmFlag;
	public PlaybackAPI(String os) {
		this.os_type = os;
	}

	@BeforeMethod // http://play-dev2.wynk.in/v2/user/content/playback?contentId=ALTBALAJI_EPISODE_1982
	public void startExtent(Method method) {
		loggerWrapper = LoggerWrapper.getInstance();
		LoggerWrapper.getInstance().myLogger.info("\n" + "\n");
		
		isDrmFlag = !((String.valueOf(contentList.get(counter)).split("\\.").length) == 1);
		
		if(isDrmFlag)
			dbo = mm.findOneWithRegex("atv", "playable_content", "_id", String.valueOf(contentList.get(counter)).split("\\.")[0], "meta.isDrm", "true", "state", "PUBLISH");
		else if(contentList.get(0).toString().contains("ALTBALAJI"))
			dbo = mm.findOneWithRegex("atv", "playable_content", "_id", String.valueOf(contentList.get(counter)).split("\\.")[0], "meta.isDrm", "false", "state", "PUBLISH");
		else
			dbo = mm.findOneWithRegex("atv", "playable_content", "_id", String.valueOf(contentList.get(counter)).split("\\.")[0], "state", "PUBLISH");
	
		try{
			contentID = mm.getStringFromMongoDocument_withoutLoggingInReports(dbo, "_id");
		}
		catch(IllegalArgumentException e) {
			String stacktrace = ExceptionUtils.getStackTrace(e);
			loggerWrapper.myLogger.error("Mongo object is null --> " + stacktrace);
		}

		String baseURI = EnvProperties.getEnvProperty("config_", "PLAY_BASE_URI");
		String endPoint = EnvProperties.getEnvProperty("config_", "PLAY_API_ENDPOINT");
		String url = baseURI + endPoint + contentID;

		Test test = method.getAnnotation(Test.class);
		ExtentTestManager.startTest(test.description() + "isDrm = " + isDrmFlag + " | " + os_type + " || GET : " + url); // new
		ExtentTestManager.getTest().assignAuthor("Nitish Bector");
		ExtentTestManager.getTest().assignCategory("1.0 - " + "Playback API");
		ExtentTestManager.getTest().setDescription(test.description() + " GET : " + url);

		headers = RestUtil.getHeadersList("PLAYBACK", os_type, url);
		
		loggerWrapper.info("--------------------- Executing the test for --------------------- : " + test.description()
				+ " | " + os_type + " | GET : " + url);

		RestUtil.requestWriter = new StringWriter();
		RestUtil.requestCapture = new PrintStream(new WriterOutputStream(RestUtil.requestWriter), true);
		RestUtil.responseWriter = new StringWriter();
		RestUtil.responseCapture = new PrintStream(new WriterOutputStream(RestUtil.responseWriter), true);

		counter++;
		requestSpecification = RestUtil.getRequestSpecification();
		requestSpecification.baseUri(baseURI); // Setup Base URI
	}

	@Test(description = "Playback API - ALTBALAJI : ", dataProvider = "urls_ALTBALAJI")
	public void playback_ALTBALAJI(String cp_type, String status, Method method) {
		loggerWrapper.info(cp_type + " " + isDrmFlag);
		SoftAssert s_assert = new SoftAssert();
		String uri = "v2/user/content/playback?contentId=" + contentID;
		String query = uri.split("\\?")[1];
		final Map<String, String> map = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query);
		
		Response resp = given().spec(requestSpecification).params(map).headers(headers).when().get(uri.split("\\?")[0]);
		loggerWrapper.info(RestUtil.requestWriter.toString());
		loggerWrapper.info(RestUtil.responseWriter.toString());
		
		RestUtil.schemaValidation(resp, "ALTBALAJI_DRM.json");
		loggerWrapper.pass("Verified Respone schema successfully");
		
		JsonPath jp = new JsonPath(resp.asString());
		resp.then().assertThat().statusCode(200);
		
		if (isDrmFlag) {
			
			String playId = mm.getStringFromMongoDocument(dbo, EnvProperties.getEnvProperty("config_", os_type + "_PLAYID_DRM"));

			s_assert.assertTrue(Helper.assert_Equals(jp.getString("playId"), playId, "playID"));
			s_assert.assertTrue(Helper.assert_Equals(jp.getString("isDrm"), "true", "isDrm"));
			s_assert.assertTrue(Helper.assert_Equals(jp.getString("playbackType"), "DRM", "playbackType"));
			
			if(os_type.equalsIgnoreCase("Android"))
			s_assert.assertTrue(Helper.assert_NotNull(jp, "drminfo.licenceUrl", "licenceUrl"));
			
			s_assert.assertTrue(Helper.assert_NotNull(jp, "drminfo.xssessionHeader", "xssessionHeader"));
			s_assert.assertTrue(Helper.assert_NotNull(jp, "drminfo.streamId", "streamId"));
			s_assert.assertTrue(Helper.assert_NotNull(jp, "drminfo.licenseValidity", "licenseValidity"));
		} else {
			String m3u8Url = mm.getStringFromMongoDocument(dbo, "meta.m3u8Url");

			s_assert.assertTrue(Helper.assert_Equals(jp.getString("playId"), m3u8Url, "playID"));
			s_assert.assertTrue(Helper.assert_Equals(jp.getString("isDrm"), "false", "isDrm"));
			s_assert.assertTrue(
					Helper.assert_Equals(jp.getBoolean("eligibleForPlayback"), Boolean.TRUE, "eligibleForPlayback"));
			s_assert.assertTrue(Helper.assert_Equals(jp.getString("playbackType"), "URL", "playbackType"));
			s_assert.assertTrue(Helper.assert_NotNull(jp, "bundleInfo", "bundleInfo"));			
		}
		String _id = mm.getStringFromMongoDocument(dbo, "_id");
		s_assert.assertTrue(Helper.assert_Equals(jp.getString("contentId"), _id, "contentId"));
		s_assert.assertAll();
	}
	
	@Factory
	public Object[] createTest() {
		String[] os = BaseTest.os.split(",");
		Object[] res;
		if (os.length == 2)
			res = new Object[] { new PlaybackAPI(os[0]), new PlaybackAPI(os[1]) };
		else
			res = new Object[] { new PlaybackAPI(os[0])};
		return res;
	}

	@Test(description = "Playback API - EROSNOW : ", dataProvider = "urls_EORSNOW")
	public void playback_EROSNOW(String cp_type, String status, Method method) {
		loggerWrapper.info(cp_type + " " + isDrmFlag);
		SoftAssert s_assert = new SoftAssert();
		String uri = "v2/user/content/playback?contentId=" + contentID;
		String query = uri.split("\\?")[1];
		final Map<String, String> map = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query);

		Response resp = given().spec(requestSpecification).params(map).headers(headers).when().get(uri.split("\\?")[0]);
		loggerWrapper.info(RestUtil.requestWriter.toString());
		loggerWrapper.info(RestUtil.responseWriter.toString());

		RestUtil.schemaValidation(resp, "ALTBALAJI.json");
		loggerWrapper.pass("Verified Respone schema successfully");

		JsonPath jp = new JsonPath(resp.asString());
		resp.then().assertThat().statusCode(200);

		s_assert.assertTrue(Helper.assert_Equals(jp.getString("isDrm"), "false", "isDrm"));
		s_assert.assertTrue(
				Helper.assert_Equals(jp.getBoolean("eligibleForPlayback"), Boolean.TRUE, "eligibleForPlayback"));
		s_assert.assertTrue(Helper.assert_Equals(jp.getString("playbackType"), "URL", "playbackType"));
		s_assert.assertTrue(Helper.assert_NotNull(jp, "playId", "playId"));
		s_assert.assertTrue(Helper.assert_NotNull(jp, "bundleInfo", "bundleInfo"));
		String _id = mm.getStringFromMongoDocument(dbo, "_id");
		s_assert.assertTrue(Helper.assert_Equals(jp.getString("contentId"), _id, "contentId"));
		s_assert.assertAll();
	}

	@Test(description = "Playback API - HOOQ : ", dataProvider = "urls_HOOQ")
	public void playback_HOOQ(String cp_type, String status, Method method) {
		loggerWrapper.info(cp_type + " " + isDrmFlag);
		SoftAssert s_assert = new SoftAssert();
		String uri = "v2/user/content/playback?contentId=" + contentID;
		String query = uri.split("\\?")[1];
		final Map<String, String> map = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query);

		Response resp = given().spec(requestSpecification).params(map).headers(headers).when().get(uri.split("\\?")[0]);
		loggerWrapper.info(RestUtil.requestWriter.toString());
		loggerWrapper.info(RestUtil.responseWriter.toString());

		RestUtil.schemaValidation(resp, "ALTBALAJI.json");
		loggerWrapper.pass("Verified Respone schema successfully");

		JsonPath jp = new JsonPath(resp.asString());
		resp.then().assertThat().statusCode(200);

		String cpContentId = mm.getStringFromMongoDocument(dbo, "cpContentId");
		String _id = mm.getStringFromMongoDocument(dbo, "_id");
		s_assert.assertTrue(Helper.assert_Equals(jp.getString("contentId"), _id, "contentId"));
		s_assert.assertTrue(
				Helper.assert_Equals(jp.getString("playId"), cpContentId, "playId"));
		s_assert.assertTrue(
				Helper.assert_Equals(jp.getBoolean("eligibleForPlayback"), Boolean.TRUE, "eligibleForPlayback"));
		s_assert.assertTrue(Helper.assert_Equals(jp.getString("playbackType"), "SDK", "playbackType"));
		s_assert.assertTrue(Helper.assert_NotNull(jp, "bundleInfo", "bundleInfo"));
		s_assert.assertAll();
	}
	
	@DataProvider(name = "urls_ALTBALAJI")
	public Object[][] urls_ALTBALAJI() throws IOException {
		contentList = null;
		counter = 0;
		ExcelUtil ex = new ExcelUtil();
		Object[][] obj = ex.readDataFromExcel("TestData", "Playback");
		contentList = new ArrayList();
		for (int i = 0; i < obj.length; i++) {
			if (((String) obj[i][0]).contains("ALTBALAJI") && ((String) obj[i][1]).equalsIgnoreCase("TRUE"))
				contentList.add(obj[i][0]);
		}
		Object[][] obj1 = new Object[contentList.size()][];
		for (int i = 0, j = 0; i < obj.length && j < contentList.size(); i++) {
			if (((String) obj[i][0]).contains("ALTBALAJI") && ((String) obj[i][1]).equalsIgnoreCase("TRUE")) {
				obj1[j] = obj[i];
				j++;
			}
		}
		return obj1;
	}

	@DataProvider(name = "urls_EORSNOW")
	public Object[][] urls_EORSNOW() throws IOException {
		contentList = null;
		counter = 0;
		ExcelUtil ex = new ExcelUtil();
		Object[][] obj = ex.readDataFromExcel("TestData", "Playback");
		contentList = new ArrayList();
		for (int i = 0; i < obj.length; i++) {
			if (((String) obj[i][0]).contains("EROSNOW") && ((String) obj[i][1]).equalsIgnoreCase("TRUE"))
				contentList.add(obj[i][0]);
		}
		Object[][] obj1 = new Object[contentList.size()][];
		for (int i = 0, j = 0; i < obj.length && j < contentList.size(); i++) {
			if (((String) obj[i][0]).contains("EROSNOW") && ((String) obj[i][1]).equalsIgnoreCase("TRUE")) {
				obj1[j] = obj[i];
				j++;
			}
		}
		return obj1;
	}

	@DataProvider(name = "urls_HOOQ")
	public Object[][] urls_HOOQ() throws IOException {
		contentList = null;
		counter = 0;
		ExcelUtil ex = new ExcelUtil();
		Object[][] obj = ex.readDataFromExcel("TestData", "Playback");
		contentList = new ArrayList();
		for (int i = 0; i < obj.length; i++) {
			if (((String) obj[i][0]).contains("HOOQ") && ((String) obj[i][1]).equalsIgnoreCase("TRUE"))
				contentList.add(obj[i][0]);
		}
		Object[][] obj1 = new Object[contentList.size()][];
		for (int i = 0, j = 0; i < obj.length && j < contentList.size(); i++) {
			if (((String) obj[i][0]).contains("HOOQ") && ((String) obj[i][1]).equalsIgnoreCase("TRUE")) {
				obj1[j] = obj[i];
				j++;
			}
		}
		return obj1;
	}
}
