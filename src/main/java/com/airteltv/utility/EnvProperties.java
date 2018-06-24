package com.airteltv.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.testng.asserts.SoftAssert;
import com.airteltv.test.BaseTest;

public class EnvProperties {

	private static String env = BaseTest.env;

	public static Properties loadProperties(String propertyFileName, String env) {
		Properties props = new Properties();
		String filePath = System.getProperty("user.dir") + "/src/main/resources/" + propertyFileName + env + ".properties";
		loadProperties(props, filePath);
		return props;
	}

	public static String getEnvProperty (String propertyFileName, String propertyName) {
		Properties envProperties = loadProperties(propertyFileName, env);
		return envProperties.getProperty(propertyName);
	}

	public static void loadProperties(Properties prop, String propertyFilePath) {
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(propertyFilePath);
			prop.load(fs);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (null != fs) {
				try {
					fs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void setEnvProperty(String folder, String propertyFileName, String propertyName, String propertyValue,
			SoftAssert obj) {
		SoftAssert s_assert = obj;
		FileOutputStream fileOut = null;
		FileInputStream fileIn = null;
		try {
			Properties props = new Properties();
			String filePath = System.getProperty("user.dir") + "/src/main/resources/" + "config_"+ env + ".properties";
			File readfile = new File(filePath);
			if (!readfile.exists())
				readfile.createNewFile();

			fileIn = new FileInputStream(filePath);
			props.load(fileIn);
			fileIn.close();

			File file = new File(filePath);
			fileOut = new FileOutputStream(file);
			props.setProperty(propertyName, propertyValue);
			props.store(fileOut, null);

		} catch (IOException e) {
			s_assert.assertTrue(false);
			e.printStackTrace();
		} finally {
			try {
				if (null != fileOut) {
					fileOut.close();
				}
				if (null != fileIn) {
					fileIn.close();
				}
			} catch (IOException e) {
				s_assert.assertTrue(false);
				e.printStackTrace();
			}
		}
	}
}

