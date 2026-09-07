package com.saucedemo.support;

import com.saucedemo.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class Waits {
    private Waits() {
    }

    public static WebElement visible(By locator) {
        return waitFor().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement clickable(By locator) {
        return waitFor().until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static boolean invisible(By locator) {
        return waitFor().until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    private static WebDriverWait waitFor() {
        return new WebDriverWait(DriverFactory.getDriver(), TestConfig.explicitWait());
    }
}
