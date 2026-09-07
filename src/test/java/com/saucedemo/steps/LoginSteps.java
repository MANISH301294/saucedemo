package com.saucedemo.steps;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.saucedemo.pages.LoginPage;
import com.saucedemo.testdata.TestData;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class LoginSteps {
    private final LoginPage loginPage = new LoginPage();

    @Given("I attempt login with invalid credentials")
    public void attemptInvalidLogin() {
        loginPage.open();
        loginPage.login(TestData.invalidUser(), TestData.invalidPassword());
    }

    @Then("I should see an authentication error message")
    public void seeAuthenticationError() {
        assertTrue(loginPage.errorMessage().contains("Username and password do not match"));
        assertTrue(loginPage.isDisplayed(), "User must remain on the login page");
    }
}
