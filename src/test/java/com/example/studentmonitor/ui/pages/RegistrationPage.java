package com.example.studentmonitor.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RegistrationPage {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    @FindBy(id = "email")
    private WebElement emailField;
    
    @FindBy(id = "password")
    private WebElement passwordField;
    
    @FindBy(id = "firstName")
    private WebElement firstNameField;
    
    @FindBy(id = "lastName")
    private WebElement lastNameField;
    
    @FindBy(id = "username")
    private WebElement usernameField;
    
    @FindBy(id = "confirmPassword")
    private WebElement confirmPasswordField;
    
    @FindBy(id = "submitBtn")
    private WebElement registerButton;
    
    @FindBy(id = "successMessage")
    private WebElement successMessage;
    
    @FindBy(css = ".alert.alert-danger")
    private WebElement errorMessage;

    public RegistrationPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        PageFactory.initElements(driver, this);
    }

    public void enterEmail(String email) {
        wait.until(ExpectedConditions.visibilityOf(emailField));
        emailField.clear();
        emailField.sendKeys(email);
    }

    public void enterPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public void enterFirstName(String firstName) {
        firstNameField.clear();
        firstNameField.sendKeys(firstName);
    }

    public void enterLastName(String lastName) {
        lastNameField.clear();
        lastNameField.sendKeys(lastName);
    }

    public void enterUsername(String username) {
        usernameField.clear();
        usernameField.sendKeys(username);
    }

    public void enterConfirmPassword(String confirmPassword) {
        confirmPasswordField.clear();
        confirmPasswordField.sendKeys(confirmPassword);
    }

    public void clickRegister() {
        // Wait for element to be clickable
        wait.until(ExpectedConditions.elementToBeClickable(registerButton));
        
        // Scroll element into view and wait a bit for any animations
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", registerButton);
        
        // Wait for element to be visible and stable
        wait.until(ExpectedConditions.visibilityOf(registerButton));
        
        try {
            // First try normal click
            registerButton.click();
            System.out.println("✅ Normal click successful");
        } catch (Exception e) {
            // If normal click fails, try JavaScript click
            System.out.println("⚠️ Normal click failed, trying JavaScript click...");
            js.executeScript("arguments[0].click();", registerButton);
            System.out.println("✅ JavaScript click successful");
        }
    }

    public String getSuccessMessage() {
        // Wait for redirect to login page and success message to appear
        try {
            wait.until(ExpectedConditions.urlContains("/login"));
            WebElement successMessageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alert-success")));
            return successMessageElement.getText();
        } catch (Exception e) {
            System.err.println("⚠ Failed to find success message: " + e.getMessage());
            throw e;
        }
    }

    public String getErrorMessage() {
        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        return errorMessage.getText();
    }
}
