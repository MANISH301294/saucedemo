package com.saucedemo.pages;

import com.saucedemo.support.Waits;
import org.openqa.selenium.By;

public class CartPage {
    private static final By CART = By.cssSelector("[data-test='shopping-cart-link']");
    private static final By CHECKOUT = By.cssSelector("[data-test='checkout']");

    public void open() {
        Waits.clickable(CART).click();
    }

    public void checkout() {
        Waits.clickable(CHECKOUT).click();
    }
}
