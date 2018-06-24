package com.airteltv.reports;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.ITestContext;
import org.testng.Reporter;

import com.relevantcodes.extentreports.ExtentReports;

public class ExtentManager {
	private static ExtentReports extent;
	private static ITestContext context;

	public synchronized static ExtentReports getInstance() {

		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy_hh.mm.ss");
		Date date = new Date();

		if (extent == null) {
			File outputDirectory = new File(context.getOutputDirectory());
			File resultDirectory = new File(System.getProperty("user.dir") + "/result-files/extent-reports");
			/*
			 * overriding location for default sure-fire reports generated when
			 * run with maven
			 */
			/*
			 * File resultDirectory = new
			 * File(outputDirectory.getParentFile(),"ExtentReports");
			 */
			extent = new ExtentReports(
					resultDirectory + File.separator + "Report" + "-" + dateFormat.format(date) + ".html", true);
			Reporter.log("Extent Report directory: " + resultDirectory, true);
		}

		return extent;
	}

	public static void setOutputDirectory(ITestContext context) {
		ExtentManager.context = context;
	}

}
