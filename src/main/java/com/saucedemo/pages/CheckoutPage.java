package com.saucedemo.pages;

import com.saucedemo.support.Waits;
import org.openqa.selenium.By;

public class CheckoutPage {
    private static final By FIRST_NAME = By.cssSelector("[data-test='firstName']");
    private static final By LAST_NAME = By.cssSelector("[data-test='lastName']");
    private static final By POSTAL_CODE = By.cssSelector("[data-test='postalCode']");
    private static final By CONTINUE = By.cssSelector("[data-test='continue']");
    private static final By FINISH = By.cssSelector("[data-test='finish']");
    private static final By COMPLETE_HEADER = By.cssSelector("[data-test='complete-header']");

    public void enterCustomerDetails(String firstName, String lastName, String postalCode) {
        Waits.visible(FIRST_NAME).sendKeys(firstName);
        Waits.visible(LAST_NAME).sendKeys(lastName);
        Waits.visible(POSTAL_CODE).sendKeys(postalCode);
        Waits.clickable(CONTINUE).click();
    }

    public void finish() {
        Waits.clickable(FINISH).click();
    }

    public boolean isComplete() {
        return Waits.visible(COMPLETE_HEADER).getText().equals("Thank you for your order!");
    }
}
