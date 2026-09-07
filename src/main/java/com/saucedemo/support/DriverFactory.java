package com.saucedemo.support;

import com.saucedemo.config.TestConfig;
import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public final class DriverFactory {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static void start() {
        WebDriver driver = switch (TestConfig.browser().toLowerCase()) {
            case "firefox" -> new FirefoxDriver(firefoxOptions());
            case "chrome" -> new ChromeDriver(chromeOptions());
            default -> throw new IllegalArgumentException(
                    "Unsupported browser: " + TestConfig.browser());
        };

        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().window().maximize();
        DRIVER.set(driver);
    }

    public static WebDriver getDriver() {
        if (DRIVER.get() == null) {
            throw new IllegalStateException("WebDriver has not been started for this scenario.");
        }
        return DRIVER.get();
    }

    public static void quit() {
        if (DRIVER.get() != null) {
            DRIVER.get().quit();
            DRIVER.remove();
        }
    }

    private static ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (TestConfig.headless()) {
            options.addArguments("--headless=new", "--window-size=1440,900");
        }
        return options;
    }

    private static FirefoxOptions firefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        if (TestConfig.headless()) {
            options.addArguments("-headless");
        }
        return options;
    }
}
