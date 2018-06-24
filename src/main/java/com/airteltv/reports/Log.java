package com.airteltv.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.airteltv.restutils.RestUtil;
import com.airteltv.test.BaseTest;

public class Log {

	final static Logger logger = LoggerFactory.getLogger(RestUtil.class);
	
	
	public static void debug(String logMessage) {
		
		System.out.println(logMessage);
		logger.debug(logMessage);
	}
	
	public static void info(String logMessage) {
		
		
		logger.info(logMessage);
	}
	
public static void error(String logMessage) {
		
		System.err.println(logMessage);
		logger.error(logMessage);
	}
	
}
