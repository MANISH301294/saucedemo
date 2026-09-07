@smoke
Feature: Purchase a SauceDemo product
  Scenario: Successful purchase of a single product
    Given I log in as a standard user
    When I add Sauce Labs Backpack to the cart
    And I check out with valid customer details
    Then I should see the order confirmation page
