package com.saucedemo.pages;

import com.saucedemo.support.Waits;
import org.openqa.selenium.By;

public class InventoryPage {
    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");
    private static final By BACKPACK_ADD =
            By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']");

    public boolean isDisplayed() {
        return Waits.visible(TITLE).getText().equals("Products");
    }

    public void addBackpack() {
        Waits.clickable(BACKPACK_ADD).click();
    }

    public String cartCount() {
        return Waits.visible(CART_BADGE).getText();
    }
}
