Feature: User Registration
  As a new user
  I want to register for an account
  So that I can access the student monitoring system

  Background:
    Given the student monitoring system is available

  Scenario: Successful user registration with valid details
    Given I am on the registration page
    When I enter email "alice@example.com"
    And I enter password "securePassword123"
    And I click the register button
    Then I should see a success message "Registration successful"
    And a new user account should be created
    And I should be redirected to the login page

  Scenario: Registration fails with existing email
    Given a user already exists with email "existing@example.com"
    And I am on the registration page
    When I enter email "existing@example.com"
    And I enter password "password123"
    And I click the register button
    Then I should see an error message "Email already taken"
    And no new user account should be created

  Scenario Outline: Registration fails with invalid input
    Given I am on the registration page
    When I enter email "<email>"
    And I enter password "<password>"
    And I click the register button
    Then I should see an error message "<error_message>"
    And no new user account should be created

    Examples:
      | email           | password | error_message                           |
      |                 | test123  | Email is required                       |
      | invalid-email   | test123  | Please enter a valid email address     |
      | test@email.com  | 123      | Password must be at least 6 characters |
      | test@email.com  |          | Password is required                    |

  Scenario: User registration with additional details
    Given I am on the registration page
    When I enter the following details:
      | Field      | Value              |
      | email      | john@example.com   |
      | password   | myPassword123      |
      | firstName  | John               |
      | lastName   | Doe                |
    And I click the register button
    Then I should see a success message "Registration successful"
    And the user profile should contain:
      | Field     | Value            |
      | email     | john@example.com |
      | firstName | John             |
      | lastName  | Doe              |
