package com.airteltv.reports;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.relevantcodes.extentreports.LogStatus;

public class LoggerWrapper{

	public final Logger myLogger = LoggerFactory.getLogger("Test");
	//public static ExtentReports extent = BaseTest.extent;
	private static LoggerWrapper instance = null;

	String className;
	String methodName;
	int lineNumber;

	public static LoggerWrapper getInstance() {
		if (instance == null) {
			instance = new LoggerWrapper();
		}
		return instance;
	}

	public void pass(String logMessage) {
		className = Thread.currentThread().getStackTrace()[2].getClassName();
		methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
		instance.myLogger.debug(className + "." + methodName + " " + lineNumber + " - " + logMessage);
		ExtentTestManager.getTest().log(LogStatus.PASS, className + "." + methodName + " " + lineNumber + " - " + logMessage);
	}

	public void info(String logMessage) {
		className = Thread.currentThread().getStackTrace()[2].getClassName();
		methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
		instance.myLogger.info(className + "." + methodName + " " + lineNumber + " - " + logMessage);
		ExtentTestManager.getTest().log(LogStatus.INFO, className + "." + methodName + " " + lineNumber + " - " + logMessage);
		
	}

	public void fail(String logMessage, Exception e) {
		className = Thread.currentThread().getStackTrace()[2].getClassName();
		methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
		instance.myLogger.error(className + "." + methodName + " " + lineNumber + " - " + logMessage + " | " + getStackTrace(e.fillInStackTrace()));
		ExtentTestManager.getTest().log(LogStatus.FAIL, className + "." + methodName + " " + lineNumber + " - " + logMessage + " | " + getStackTrace(e.fillInStackTrace()));
	}

	// prevent cloning
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}
}
