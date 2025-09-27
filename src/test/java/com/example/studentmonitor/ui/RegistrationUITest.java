package com.example.studentmonitor.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.studentmonitor.model.User;
import com.example.studentmonitor.service.UserService;
import com.example.studentmonitor.ui.pages.RegistrationPage;

class RegistrationUITest extends BaseUITest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("UI Test 3: Successful user registration")
    void testSuccessfulRegistration() {
        // Arrange
        driver.get(baseUrl + "/register");
        RegistrationPage registrationPage = new RegistrationPage(driver, wait);

        // Act
        String uniqueEmail = "newuser" + System.currentTimeMillis() + "@example.com";
        String uniqueUsername = "user" + System.currentTimeMillis();
        registrationPage.enterFirstName("John");
        registrationPage.enterLastName("Doe");
        registrationPage.enterUsername(uniqueUsername);
        registrationPage.enterEmail(uniqueEmail);
        registrationPage.enterPassword("password123");
        registrationPage.enterConfirmPassword("password123");
        registrationPage.clickRegister();

        // Assert
        String successMessage = registrationPage.getSuccessMessage();
        assertTrue(successMessage.contains("successful") || successMessage.contains("created"), 
                   "Should show success message after registration");
    }

    @Test
    @DisplayName("UI Test 4: Registration fails with existing email")
    void testRegistrationWithExistingEmail() {
        // Arrange - First create a user with the email we'll try to duplicate
        User existingUser = new User();
        existingUser.setFirstName("Existing");
        existingUser.setLastName("User");
        existingUser.setUsername("existinguser");
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword("password123");
        try {
            userService.registerUser(existingUser);
        } catch (Exception e) {
            // User might already exist, which is fine for this scenario
        }

        driver.get(baseUrl + "/register");
        RegistrationPage registrationPage = new RegistrationPage(driver, wait);

        // Act - Try to register with the same email
        registrationPage.enterFirstName("Jane");
        registrationPage.enterLastName("Smith");
        registrationPage.enterUsername("newuser");
        registrationPage.enterEmail("existing@example.com");
        registrationPage.enterPassword("password123");
        registrationPage.enterConfirmPassword("password123");
        registrationPage.clickRegister();

        // Assert
        String errorMessage = registrationPage.getErrorMessage();
        assertTrue(errorMessage.contains("already") || errorMessage.contains("exists") || 
                  errorMessage.contains("taken") || errorMessage.contains("registered"), 
                   "Should show error for existing email");
    }
}