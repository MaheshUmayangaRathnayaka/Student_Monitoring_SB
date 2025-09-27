package com.example.studentmonitor.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.studentmonitor.ui.pages.RegistrationPage;

public class RegistrationUITest extends BaseUITest {

    @Test
    @DisplayName("UI Test 3: Successful user registration")
    void testSuccessfulRegistration() {
        // Arrange
        driver.get(baseUrl + "/register");
        RegistrationPage registrationPage = new RegistrationPage(driver, wait);

        // Act
        String uniqueEmail = "newuser" + System.currentTimeMillis() + "@example.com";
        registrationPage.enterEmail(uniqueEmail);
        registrationPage.enterPassword("password123");
        registrationPage.enterFirstName("John");
        registrationPage.enterLastName("Doe");
        registrationPage.clickRegister();

        // Assert
        String successMessage = registrationPage.getSuccessMessage();
        assertTrue(successMessage.contains("successful") || successMessage.contains("created"), 
                   "Should show success message after registration");
    }

    @Test
    @DisplayName("UI Test 4: Registration fails with existing email")
    void testRegistrationWithExistingEmail() {
        // Arrange
        driver.get(baseUrl + "/register");
        RegistrationPage registrationPage = new RegistrationPage(driver, wait);

        // Act
        registrationPage.enterEmail("existing@example.com");
        registrationPage.enterPassword("password123");
        registrationPage.enterFirstName("Jane");
        registrationPage.enterLastName("Smith");
        registrationPage.clickRegister();

        // Assert
        String errorMessage = registrationPage.getErrorMessage();
        assertTrue(errorMessage.contains("already") || errorMessage.contains("exists"), 
                   "Should show error for existing email");
    }
}