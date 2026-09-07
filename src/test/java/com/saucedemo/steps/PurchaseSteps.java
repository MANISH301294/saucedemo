package com.saucedemo.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.testdata.TestData;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PurchaseSteps {
    private final LoginPage loginPage = new LoginPage();
    private final InventoryPage inventoryPage = new InventoryPage();
    private final CartPage cartPage = new CartPage();
    private final CheckoutPage checkoutPage = new CheckoutPage();

    @Given("I log in as a standard user")
    public void logInAsStandardUser() {
        loginPage.open();
        loginPage.login(TestData.standardUser(), TestData.standardPassword());

        assertTrue(inventoryPage.isDisplayed(), "Products page should be visible after login");
    }

    @When("I add Sauce Labs Backpack to the cart")
    public void addBackpack() {
        inventoryPage.addBackpack();

        assertEquals("1", inventoryPage.cartCount(), "Cart badge should update");
    }

    @When("I check out with valid customer details")
    public void checkout() {
        cartPage.open();
        cartPage.checkout();
        checkoutPage.enterCustomerDetails(
                TestData.firstName(), TestData.lastName(), TestData.postalCode());
        checkoutPage.finish();
    }

    @Then("I should see the order confirmation page")
    public void seeOrderConfirmation() {
        assertTrue(checkoutPage.isComplete(), "Checkout complete page should be visible");
    }
}
