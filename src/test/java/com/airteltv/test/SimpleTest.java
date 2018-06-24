package com.airteltv.test;

import org.testng.annotations.Test;
public class SimpleTest extends BaseTest{
	
@Test
public void test() {
	String env = System.getProperties().getProperty("envname");
	String os = System.getProperties().getProperty("os");
	
		System.out.println(env + " -----  " + os);
	}
}
