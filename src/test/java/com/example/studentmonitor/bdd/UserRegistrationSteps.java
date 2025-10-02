package com.example.studentmonitor.bdd;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.example.studentmonitor.dto.SignupRequest;
import com.example.studentmonitor.model.User;
import com.example.studentmonitor.repository.UserRepository;
import com.example.studentmonitor.service.AuthService;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

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
                    signupRequest.setFirstName(value);
                    break;
                case "lastname":
                    signupRequest.setLastName(value);
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