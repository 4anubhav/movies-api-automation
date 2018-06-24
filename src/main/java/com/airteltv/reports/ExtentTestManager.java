package com.airteltv.reports;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.relevantcodes.extentreports.model.Log;
import com.relevantcodes.extentreports.model.ScreenCapture;
import com.relevantcodes.extentreports.model.Test;
import com.relevantcodes.extentreports.view.ScreenshotHtml;

public class ExtentTestManager {  // new
	
	 private static Test test; 
	 private static LogStatus runStatus = LogStatus.UNKNOWN;
	 
    static Map<Integer, ExtentTest> extentTestMap = new HashMap<Integer, ExtentTest>();
    
    private static ExtentReports extent = ExtentManager.getInstance();

    public static synchronized ExtentTest getTest() {
        return extentTestMap.get((int) (long) (Thread.currentThread().getId()));
    }

    public static synchronized void endTest() {
        extent.endTest(extentTestMap.get((int) (long) (Thread.currentThread().getId())));
    }

    public static synchronized ExtentTest startTest(String testName) {
        return startTest(testName, "");
    }

    public static synchronized ExtentTest startTest(String testName, String desc) {
        ExtentTest test = extent.startTest(testName, desc);
        extentTestMap.put((int) (long) (Thread.currentThread().getId()), test);

        return test;
    }
    
    
    public static String addScreenCapture(String imagePath) {
    	
    	test = new Test();
        String screenCaptureHtml = isPathRelative(imagePath)
                ? ScreenshotHtml.getSource(imagePath).replace("file:///", "")
                        : ScreenshotHtml.getSource(imagePath);
        
        ScreenCapture img = new ScreenCapture();
        img.setSource(screenCaptureHtml);
        img.setTestName(test.getName());
        img.setTestId(test.getId());
        
        test.setScreenCapture(img);

        return screenCaptureHtml;
    }
    
    
    /**
     * <p>
     * Determines if path of the file is relative or absolute
     * 
     * @param path
     *      Path of the file
     * 
     * @return
     *      Boolean
     */
    private static Boolean isPathRelative(String path) {
        if (path.indexOf("http") == 0 || !new File(path).isAbsolute()) {
            return true;
        }
        
        return false;
    }

    public static void log(LogStatus logStatus, String stepName, String details) {
        Log evt = new Log();
        
        evt.setLogStatus(logStatus);
        evt.setStepName(stepName == null ? null : stepName.trim()); 
        evt.setDetails(details == null ? "" : details.trim());

        test.setLog(evt);
        
        test.trackLastRunStatus();
        runStatus = test.getStatus();
    }
    
}
