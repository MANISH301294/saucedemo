package com.saucedemo.config;
import java.time.Duration;
/** Resolution order: JVM property, environment variable, safe default. */
public final class TestConfig {
 private TestConfig() { }
 public static String baseUrl(){return value("baseUrl","BASE_URL","https://www.saucedemo.com/");} public static String browser(){return value("browser","BROWSER","chrome");} public static boolean headless(){return Boolean.parseBoolean(value("headless","HEADLESS","true"));} public static Duration explicitWait(){return Duration.ofSeconds(Long.parseLong(value("explicitWaitSeconds","EXPLICIT_WAIT_SECONDS","10")));} public static String dbUrl(){return value("dbUrl","DB_URL","");} public static String dbUser(){return value("dbUser","DB_USER","");} public static String dbPassword(){return value("dbPassword","DB_PASSWORD","");}
 private static String value(String property,String environment,String fallback){String configured=System.getProperty(property);return configured!=null&&!configured.isBlank()?configured:System.getenv().getOrDefault(environment,fallback);}
}
