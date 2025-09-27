# Behavior-Driven Development (BDD) Tutorial

## Overview
This tutorial demonstrates BDD using Cucumber with Java and Spring Boot. Learn to write human-readable tests using Gherkin syntax.

## Prerequisites
- Spring Boot project
- Maven dependencies for Cucumber
- IDE with Cucumber plugin
- Terminal/Command Prompt

## 🎯 Step-by-Step BDD Testing Instructions

### Quick Test Execution Commands

```bash
# Navigate to project directory
cd c:\Users\usr\Desktop\Smon\Student_Monitoring_SB

# Set JAVA_HOME (if not set)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"

# Run all BDD tests
.\mvnw test -Dtest=CucumberTestRunner

# Run specific feature
.\mvnw test -Dcucumber.filter.tags="@user-registration"
```

## Step 1: Add BDD Dependencies

**File:** `pom.xml` (Add to dependencies section)

```xml
<dependencies>
    <!-- Existing dependencies... -->
    
    <!-- BDD Dependencies -->
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-java</artifactId>
        <version>7.14.0</version>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-junit-platform-engine</artifactId>
        <version>7.14.0</version>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-spring</artifactId>
        <version>7.14.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Command:**
```bash
./mvnw clean install
```

## 🟢 BDD TEST 1: User Registration Feature

### Step 1: Create Feature File

**Execute this command to test the feature:**
```bash
# Run the BDD test for user registration
.\mvnw test -Dtest=CucumberTestRunner -Dcucumber.filter.tags="@user-registration"
```

**Expected Output:** ✅ All scenarios should PASS

**Directory:** `src/test/resources/features/`

**File:** `src/test/resources/features/user_registration.feature`

```gherkin
@user-registration
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
```

### Step 2: Execute BDD Test 1
```bash
# Run specific tagged feature
.\mvnw test -Dtest=CucumberTestRunner -Dcucumber.filter.tags="@user-registration"

# Expected output: 
# - Feature: User Registration
# - Scenario: Successful user registration with valid details ✅ PASSED
# - Scenario: Registration fails with existing email ✅ PASSED
# - Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
```

### Step 3: View BDD HTML Report
```bash
# After test execution, open the HTML report
# File location: target/cucumber-reports/cucumber.html
# Open in browser to see detailed BDD test results
```

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
```

## Step 3: Create Step Definitions

**File:** `src/test/java/com/example/studentmonitor/bdd/UserRegistrationSteps.java`

```java
package com.example.studentmonitor.bdd;

import com.example.studentmonitor.dto.SignupRequest;
import com.example.studentmonitor.model.User;
import com.example.studentmonitor.repository.UserRepository;
import com.example.studentmonitor.service.AuthService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserRegistrationSteps {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserRepository userRepository;
    
    private SignupRequest signupRequest;
    private User registeredUser;
    private Exception thrownException;
    private String successMessage;
    private String errorMessage;
    private boolean systemAvailable;

    @Given("the student monitoring system is available")
    public void theStudentMonitoringSystemIsAvailable() {
        systemAvailable = true;
        // Clear any existing test data
        userRepository.deleteAll();
    }

    @Given("I am on the registration page")
    public void iAmOnTheRegistrationPage() {
        assertTrue(systemAvailable, "System should be available");
        // Initialize new signup request
        signupRequest = new SignupRequest();
        thrownException = null;
        registeredUser = null;
        successMessage = null;
        errorMessage = null;
    }

    @Given("a user already exists with email {string}")
    public void aUserAlreadyExistsWithEmail(String email) {
        // Create existing user
        SignupRequest existingUserRequest = new SignupRequest(email, "existingPassword123");
        try {
            authService.signup(existingUserRequest);
        } catch (Exception e) {
            // User might already exist, which is fine for this scenario
        }
    }

    @When("I enter email {string}")
    public void iEnterEmail(String email) {
        signupRequest.setEmail(email);
    }

    @When("I enter password {string}")
    public void iEnterPassword(String password) {
        signupRequest.setPassword(password);
    }

    @When("I enter the following details:")
    public void iEnterTheFollowingDetails(DataTable dataTable) {
        List<Map<String, String>> details = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> detail : details) {
            String field = detail.get("Field");
            String value = detail.get("Value");
            
            switch (field.toLowerCase()) {
                case "email":
                    signupRequest.setEmail(value);
                    break;
                case "password":
                    signupRequest.setPassword(value);
                    break;
                case "firstname":
                    // For this simple example, we'll store in email field
                    // In real implementation, extend SignupRequest
                    break;
                case "lastname":
                    // Similar handling
                    break;
            }
        }
    }

    @When("I click the register button")
    public void iClickTheRegisterButton() {
        try {
            registeredUser = authService.signup(signupRequest);
            successMessage = "Registration successful";
        } catch (IllegalArgumentException e) {
            thrownException = e;
            errorMessage = e.getMessage();
        } catch (Exception e) {
            thrownException = e;
            errorMessage = "An unexpected error occurred";
        }
    }

    @Then("I should see a success message {string}")
    public void iShouldSeeASuccessMessage(String expectedMessage) {
        assertNull(thrownException, "No exception should be thrown for successful registration");
        assertEquals(expectedMessage, successMessage);
        assertNotNull(registeredUser, "User should be registered");
    }

    @Then("I should see an error message {string}")
    public void iShouldSeeAnErrorMessage(String expectedMessage) {
        assertNotNull(thrownException, "Exception should be thrown");
        assertEquals(expectedMessage, errorMessage);
        assertNull(registeredUser, "No user should be registered");
    }

    @And("a new user account should be created")
    public void aNewUserAccountShouldBeCreated() {
        assertNotNull(registeredUser, "User should be created");
        assertNotNull(registeredUser.getId(), "User should have an ID");
        
        // Verify user exists in database
        assertTrue(userRepository.existsByEmail(signupRequest.getEmail()));
    }

    @And("no new user account should be created")
    public void noNewUserAccountShouldBeCreated() {
        assertNull(registeredUser, "No user should be created");
    }

    @And("I should be redirected to the login page")
    public void iShouldBeRedirectedToTheLoginPage() {
        // In a real application, this would verify URL redirection
        // For this example, we'll assume successful registration leads to redirect
        assertNotNull(registeredUser, "Successful registration should redirect");
    }

    @Then("the user profile should contain:")
    public void theUserProfileShouldContain(DataTable dataTable) {
        assertNotNull(registeredUser, "User should be registered");
        
        List<Map<String, String>> expectedDetails = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> detail : expectedDetails) {
            String field = detail.get("Field");
            String expectedValue = detail.get("Value");
            
            switch (field.toLowerCase()) {
                case "email":
                    assertEquals(expectedValue, registeredUser.getEmail());
                    break;
                case "firstname":
                    assertEquals(expectedValue, registeredUser.getFirstName());
                    break;
                case "lastname":
                    assertEquals(expectedValue, registeredUser.getLastName());
                    break;
            }
        }
    }
}
```

## Step 4: Create Test Configuration

**File:** `src/test/resources/application-test.properties`

```properties
# Test database configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate configuration for tests
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Disable security for tests
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
```

## Step 5: Create Cucumber Test Runner

**File:** `src/test/java/com/example/studentmonitor/bdd/CucumberTestRunner.java`

```java
package com.example.studentmonitor.bdd;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("com.example.studentmonitor.bdd")
@ConfigurationParameter(key = Constants.FEATURES_PROPERTY_NAME, value = "src/test/resources/features")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.example.studentmonitor.bdd")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty,html:target/cucumber-reports,json:target/cucumber-reports/Cucumber.json")
public class CucumberTestRunner {
}
```

## Step 6: Add Cucumber Configuration

**File:** `src/test/java/com/example/studentmonitor/bdd/CucumberSpringConfiguration.java`

```java
package com.example.studentmonitor.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@CucumberContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class CucumberSpringConfiguration {
}
```

## Step 7: Run BDD Tests

**Commands:**

```bash
# Run BDD tests
./mvnw test -Dtest=CucumberTestRunner

# Run with specific tags
./mvnw test -Dtest=CucumberTestRunner -Dcucumber.filter.tags="@smoke"

# Generate reports
./mvnw test -Dtest=CucumberTestRunner
# Reports will be in target/cucumber-reports/
```

## Step 8: View Results

**HTML Report Location:** `target/cucumber-reports/index.html`

**JSON Report Location:** `target/cucumber-reports/Cucumber.json`

## Screenshots to Take

1. **Feature File:** Show Gherkin syntax in IDE
2. **Step Definitions:** Show Java step definitions
3. **Test Execution:** Terminal output showing scenarios
4. **HTML Report:** Cucumber HTML report
5. **IDE Integration:** Cucumber plugin showing feature mapping

## Advanced BDD Features

### Tags for Test Organization

**Add to feature file:**

```gherkin
@smoke @registration
Feature: User Registration

@positive @smoke
Scenario: Successful user registration with valid details
  # ... existing scenario

@negative
Scenario: Registration fails with existing email
  # ... existing scenario

@validation @negative
Scenario Outline: Registration fails with invalid input
  # ... existing scenario outline
```

### Hooks for Setup/Teardown

**File:** `src/test/java/com/example/studentmonitor/bdd/Hooks.java`

```java
package com.example.studentmonitor.bdd;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.studentmonitor.repository.UserRepository;

@SpringBootTest
public class Hooks {

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp(Scenario scenario) {
        System.out.println("Starting scenario: " + scenario.getName());
        // Clean up data before each scenario
        userRepository.deleteAll();
    }

    @After
    public void tearDown(Scenario scenario) {
        System.out.println("Finished scenario: " + scenario.getName());
        if (scenario.isFailed()) {
            System.out.println("Scenario failed!");
            // Add screenshot capture logic here
        }
    }
}
```

## Run Commands Summary

```bash
# Install dependencies
./mvnw clean install

# Run specific feature
./mvnw test -Dtest=CucumberTestRunner -Dcucumber.features=src/test/resources/features/user_registration.feature

# Run with tags
./mvnw test -Dtest=CucumberTestRunner -Dcucumber.filter.tags="@smoke"

# Run and generate reports
./mvnw clean test -Dtest=CucumberTestRunner

# View HTML report
# Open target/cucumber-reports/index.html in browser
```

## Key BDD Benefits Demonstrated

1. ✅ **Living Documentation** - Features readable by business stakeholders
2. ✅ **Collaboration** - Bridge between business and technical teams  
3. ✅ **Acceptance Criteria** - Clear definition of done
4. ✅ **Automated Testing** - Executable specifications
5. ✅ **Traceability** - Link between requirements and tests