package com.airteltv.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.airteltv.reports.ExtentManager;
import com.airteltv.reports.ExtentTestManager;
import com.airteltv.reports.LoggerWrapper;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.LogStatus;

public class BaseTest {
	public static ExtentReports extent;
	public static String env = System.getProperties().getProperty("envname");
	public static String os = System.getProperties().getProperty("os");
	
	private LoggerWrapper loggerWrapper = LoggerWrapper.getInstance();
	public static String testName;

	@SuppressWarnings("deprecation")
	@BeforeSuite
	public synchronized void extentSetup(ITestContext context) throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy_hh.mm.ss");
		Date date = new Date();

		ExtentManager.setOutputDirectory(context);
		extent = ExtentManager.getInstance();

		extent.addSystemInfo("Environment", env);
		extent.addSystemInfo("OS", os);
		extent.addSystemInfo("Host Name", "Nitish Bector");
		extent.loadConfig(new File(System.getProperty("user.dir") + "/src/main/resources/extent-congif.xml"));
		//loggerWrapper.info("Setting up environment");
	}

	@AfterMethod
	public synchronized void afterEachTestMethod(ITestResult result) {

		testName = result.getMethod().getMethodName();
		ExtentTestManager.getTest().getTest().setStartedTime(getTime(result.getStartMillis())); // new
		ExtentTestManager.getTest().getTest().setEndedTime(getTime(result.getEndMillis())); // new

		for (String group : result.getMethod().getGroups()) {
			ExtentTestManager.getTest().assignCategory(group); // new
		}

		if (result.getStatus() == 1) {
			ExtentTestManager.getTest().log(LogStatus.PASS, "TEST PASSED"); // new
		} else if (result.getStatus() == 2) {
			ExtentTestManager.getTest().log(LogStatus.FAIL, getStackTrace(result.getThrowable()));

		}

		else if (result.getStatus() == 3) {
			ExtentTestManager.getTest().log(LogStatus.SKIP, getStackTrace(result.getThrowable())); // new

		}

		ExtentTestManager.endTest(); // new
	}

	protected String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}

	private Date getTime(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.getTime();
	}
	
	@AfterSuite
	public void generateReport() {
		
		extent.flush();
		extent.close();
	}
	
	public static void main(String[] args) {
		
			System.out.println(env);
	}
}
