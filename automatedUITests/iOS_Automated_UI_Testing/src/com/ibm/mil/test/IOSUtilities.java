package com.ibm.mil.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.communication.device.DeviceVariation;

public class IOSUtilities {

	public static final String ENCODING = "UTF-8";
	public static final long WAIT_TIME = 5000;
	public static final String PROPERTIES_FILE_NAME= "ios.automated.testing.properties";
	public static final int DRIVER_RETRIES = 10;
	public static final int DRIVER_WAIT_TIME = 2000; //2 seconds
	public static final int ELEMENT_WAIT_ITERATIONS = 20;
	
	private Properties iosProps = null;

	public IOSUtilities() {
		super();
		try {
			iosProps = new Properties();
			File props = new File(PROPERTIES_FILE_NAME);
			System.out.println("Attempting to load test.properties file : " + props.getAbsolutePath());
			iosProps.load(new BufferedReader(new InputStreamReader(new FileInputStream(props.getAbsolutePath()), ENCODING)));
		} catch(Exception ex) {
			System.out.println("Failed to open " + PROPERTIES_FILE_NAME + ", cannot continue.");
		}
	}
	
	private String getKeyValue(String key, String defaultValue) {
		//Using the value from the properties file, if its set, otherwise default to defaultValue.
		String value = iosProps.containsKey(key) ? (String) iosProps.get(key) : defaultValue;
		//If there is a system property set (using -D on the command line) hten lets use that instead.
		value = System.getProperty(key, value);
		return value;
	}
	
	//set up the ios web driver
	public WebDriver iosTestSetupWebDriver(WebDriver driver) throws Exception {
		// create a selenium desiredCapabilities object with the right values.
		String appName = getKeyValue("APP_NAME", "ReadyAppPT");
		String appVersion = getKeyValue("APP_VERSION", "0.0.1");
		String port = getKeyValue("PORT", "4455");
		
		IOSCapabilities cap = IOSCapabilities.iphone(appName, appVersion);
		cap.setCapability("simulatorVersion", "8.1");
		//caps.setCapability("", "");
		cap.setCapability(IOSCapabilities.VARIATION, DeviceVariation.iPhone6);
		//cap.setCapability(IOSCapabilities.UUID, "35C6E562-8A34-41A7-BB1D-A84BADFA8769");//DeviceVariation.iPhone6);
		
		String url = "http://localhost:" + port + "/wd/hub";
		System.out.println("Connecting to ios selenium server at url: " + url + ", using appname: " + appName + 
				", and version: " + appVersion);
		
		// start the application
		for (int i = 0 ; i < IOSUtilities.DRIVER_RETRIES ; ++i ) {
			try {
				driver = new RemoteIOSDriver(new URL(url), cap);//createRemoteWebDriver("iphone", cap, new URL(url));
				break;
			} catch (Exception ex) {
				System.out.println("WebDriver creation failed, retrying, try " + (i+1) + " of " + IOSUtilities.DRIVER_RETRIES);
				try { Thread.sleep(IOSUtilities.DRIVER_WAIT_TIME) ; } catch (Exception sleepEx) {}
				if (i == (IOSUtilities.DRIVER_RETRIES -1)) {
					throw ex;
				}
			}
		}
		
		try { Thread.sleep(5000); } catch (InterruptedException e) {}
		return driver;
	}
	
	public void  closeKeyboard(WebDriver driver){
		RemoteIOSDriver riosd = (RemoteIOSDriver) driver;
		//Dimension size = riosd.getScreenSize(); //here dimension contains the max horizontal and vertical pixel values.
		String jsScriptText = "UIATarget.localTarget().frontMostApp().keyboard().buttons()['Done'].tap();";
		riosd.executeScript(jsScriptText, new Object[]{});
	}

	
	protected WebDriver createRemoteWebDriver(String browserName, DesiredCapabilities desiredCapabilities, 
			URL remoteUrl) {
		IOSCapabilities safari=null;
		if("iphone".equals(browserName)){
			safari = IOSCapabilities.iphone("Safari");
		}else{
			safari = IOSCapabilities.ipad("Safari");
		}
		return new RemoteIOSDriver(remoteUrl,safari);
	}

	public void iosTestTeardownDriver(WebDriver driver) {
		if (driver != null) {
			 try { driver.quit(); } catch (Exception ex) {}
		}
	}
	
	public WebElement waitForElement(WebDriver driver, String elementId) {
		return waitForElement(driver, elementId, ELEMENT_WAIT_ITERATIONS);
	}
	
	public WebElement waitForElement(WebDriver driver, String elementId, int waitIterations) {
		//int iterations = ELEMENT_WAIT_ITERATIONS;
		WebElement elem = null;
		for (int i = 0 ; i < waitIterations ; ++i ) {
			try {
				elem = driver.findElement(By.id(elementId));
				//elem.click();
				break;
			} catch (Exception ex) {
				System.out.println("got exception ex on loop looking for element: " + elementId + 
						", on iterator: " + i + ", message: " + ex.getMessage());
				if (i == (waitIterations - 1)) {
					throw (ex);
				} else {
					try { Thread.sleep(WAIT_TIME); } catch (Exception ex2) {}
				}
			}
		}
		return elem;
	}
	public List<WebElement> waitForListElements(WebDriver driver, String className, int waitIterations) {
		//int iterations = ELEMENT_WAIT_ITERATIONS;
		List<WebElement> elem = null;
		for (int i = 0 ; i < waitIterations ; ++i ) {
			try {
				elem = driver.findElements(By
						.className(className));
				//elem.click();
				break;
			} catch (Exception ex) {
				System.out.println("got exception ex on loop looking for List of elements: " + className + 
						", on iterator: " + i + ", message: " + ex.getMessage());
				if (i == (waitIterations - 1)) {
					throw (ex);
				} else {
					try { Thread.sleep(WAIT_TIME); } catch (Exception ex2) {}
				}
			}
		}
		return elem;
	}
} 
