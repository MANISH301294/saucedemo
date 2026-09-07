@smoke
Feature: Login validation
  Scenario: Invalid login shows error
    Given I attempt login with invalid credentials
    Then I should see an authentication error message
