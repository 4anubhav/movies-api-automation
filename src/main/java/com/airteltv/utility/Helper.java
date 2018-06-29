package com.airteltv.utility;

import java.awt.List;

import com.airteltv.reports.LoggerWrapper;

import io.restassured.path.json.JsonPath;

public class Helper {
	private static LoggerWrapper loggerWrapper = LoggerWrapper.getInstance();
	
	public String extractJSONPath(String json, String jsonPath) {
		JsonPath jp = new JsonPath(json);
		try {
			String jsonString = jp.getString("playId");
			loggerWrapper.info("key value is for " + jsonPath + " : " + jsonString);
			return jsonString;
		} catch (NullPointerException e) {
			loggerWrapper.fail(jsonPath + " is invalid ", e);
			return null;
		}
	}
	
	public java.util.List<Object> extractJSONPathAsList(String json, String jsonPath) {
		JsonPath jp = new JsonPath(json);
		try {
			java.util.List<Object> jsonString = jp.getList("playId");
			loggerWrapper.info("key value is for " + jsonPath + " : " + jsonString);
			return jsonString;
		} catch (NullPointerException e) {
			loggerWrapper.fail(jsonPath + " is invalid ", e);
			return null;
		}
	}
	
	public static boolean assert_Equals(Object actualString, Object strToBeCompared, String message) {
		Boolean status = actualString.equals(strToBeCompared);
		if (status) {
			loggerWrapper.pass("Verified " + message + " successfully" + " | Expected --> " + strToBeCompared
					+ ", Actual --> " + actualString);
		} else
			loggerWrapper.fail(
					message + " is incorrect" + " | Expected --> " + strToBeCompared + ", Actual --> " + actualString,
					new RuntimeException(" Incorrect " + message));

		return status;
	}
	
	public static boolean assert_NotNull(JsonPath jp, String jsonpath, String message) {
		Boolean status = false;
		
		try {
			String key = jp.getString(jsonpath);
			status = !key.equals(null);
			loggerWrapper.pass(message + " exists and is not null - " + key);
		} catch (NullPointerException e) {
			loggerWrapper.fail("Inalid json path :" + jsonpath, e);
		}
		return status;
	}
}
