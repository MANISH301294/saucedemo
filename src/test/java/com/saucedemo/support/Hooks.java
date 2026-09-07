package com.saucedemo.support;
import io.cucumber.java.*; import org.openqa.selenium.*; import java.util.logging.Logger;
public class Hooks {private static final Logger LOG=Logger.getLogger(Hooks.class.getName());@Before public void openBrowser(){DriverFactory.start();LOG.info("Browser started");}@After public void closeBrowser(Scenario scenario){try{if(scenario.isFailed()){byte[] screenshot=((TakesScreenshot)DriverFactory.getDriver()).getScreenshotAs(OutputType.BYTES);scenario.attach(screenshot,"image/png","failure-screenshot");}}finally{DriverFactory.quit();}}}
