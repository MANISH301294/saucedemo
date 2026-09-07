package com.saucedemo.support;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import java.util.logging.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Hooks {
    private static final Logger LOG = Logger.getLogger(Hooks.class.getName());

    @Before
    public void openBrowser() {
        DriverFactory.start();
        LOG.info("Browser started");
    }

    @After
    public void closeBrowser(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                byte[] screenshot = ((TakesScreenshot) DriverFactory.getDriver())
                        .getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "failure-screenshot");
            }
        } finally {
            DriverFactory.quit();
        }
    }
}
