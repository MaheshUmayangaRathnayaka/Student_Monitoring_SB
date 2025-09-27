# Test Automation & Continuous Integration Tutorial

## Overview
This tutorial covers UI testing with Selenium, API testing with REST Assured, and CI/CD with GitHub Actions. Learn automated testing for web applications.

## 🎯 Step-by-Step Selenium Testing Instructions

### Quick Test Execution Commands

```bash
# Navigate to project directory
cd c:\Users\usr\Desktop\Smon\Student_Monitoring_SB

# Set JAVA_HOME (if not set)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"

# Run all Selenium UI tests
.\mvnw test -Dtest=*UITest

# Run specific UI test class
.\mvnw test -Dtest=LoginUITest
```

## Part 1: Selenium UI Testing

### Prerequisites
- Chrome/Firefox browser
- WebDriver dependencies
- Spring Boot application running

### Step 1: Add Selenium Dependencies

**File:** `pom.xml`

```xml
<dependencies>
    <!-- Existing dependencies... -->
    
    <!-- Spring Boot Actuator for health checks -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <!-- Selenium Dependencies -->
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>4.15.0</version>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>io.github.bonigarcia</groupId>
        <artifactId>webdrivermanager</artifactId>
        <version>5.6.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Step 2: Create UI Test Base Class

**File:** `src/test/java/com/example/studentmonitor/ui/BaseUITest.java`

```java
package com.example.studentmonitor.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.File;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseUITest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    
    @LocalServerPort
    private int port;
    
    protected String baseUrl;
    private String userDataDir;

    @BeforeAll
    static void setupClass() {
        // Disable ChromeDriver logs
        System.setProperty("webdriver.chrome.silentOutput", "true");
        Logger.getLogger("org.openqa.selenium").setLevel(Level.WARNING);
        
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        
        // Check if running in CI environment
        String isCI = System.getProperty("CI", System.getenv("CI"));
        boolean isCIEnvironment = "true".equals(isCI);
        
        if (isCIEnvironment) {
            // CI Environment Configuration
            options.addArguments("--headless=new");  // Use new headless mode
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-web-security");
            options.addArguments("--disable-features=VizDisplayCompositor");
            options.addArguments("--disable-background-timer-throttling");
            options.addArguments("--disable-renderer-backgrounding");
            options.addArguments("--disable-backgrounding-occluded-windows");
            options.addArguments("--disable-client-side-phishing-detection");
            options.addArguments("--disable-default-apps");
            options.addArguments("--disable-hang-monitor");
            options.addArguments("--disable-popup-blocking");
            options.addArguments("--disable-prompt-on-repost");
            options.addArguments("--disable-sync");
            options.addArguments("--disable-translate");
            options.addArguments("--metrics-recording-only");
            options.addArguments("--no-first-run");
            options.addArguments("--safebrowsing-disable-auto-update");
            options.addArguments("--enable-automation");
            options.addArguments("--password-store=basic");
            options.addArguments("--use-mock-keychain");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--remote-debugging-port=9222");
            
            // CRITICAL FIX: Create unique user data directory for each test session
            userDataDir = createUniqueUserDataDir();
            options.addArguments("--user-data-dir=" + userDataDir);
            
            // Set additional preferences
            options.setExperimentalOption("useAutomationExtension", false);
            options.setExperimentalOption("excludeSwitches", 
                java.util.Arrays.asList("enable-automation"));
            
        } else {
            // Local Development Configuration
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-web-security");
            options.addArguments("--allow-running-insecure-content");
            
            // Create user data dir for local testing too
            userDataDir = createUniqueUserDataDir();
            options.addArguments("--user-data-dir=" + userDataDir);
        }
        
        // Common settings
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-features=VizDisplayCompositor");
        
        try {
            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            baseUrl = "http://localhost:" + port;
            
            // Set timeouts
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            
        } catch (Exception e) {
            System.err.println("Failed to initialize ChromeDriver: " + e.getMessage());
            cleanupUserDataDir();
            throw e;
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error closing driver: " + e.getMessage());
            }
        }
        
        // Clean up user data directory
        cleanupUserDataDir();
    }
    
    private String createUniqueUserDataDir() {
        String tempDir = System.getProperty("java.io.tmpdir");
        String uniqueDir = tempDir + File.separator + 
                          "chrome-user-data-" + 
                          System.currentTimeMillis() + "-" + 
                          Thread.currentThread().getId() + "-" +
                          (int)(Math.random() * 10000);
        
        File dir = new File(uniqueDir);
        dir.mkdirs();
        
        System.out.println("Created Chrome user data directory: " + uniqueDir);
        return uniqueDir;
    }
    
    private void cleanupUserDataDir() {
        if (userDataDir != null) {
            try {
                File dir = new File(userDataDir);
                if (dir.exists()) {
                    deleteDirectory(dir);
                    System.out.println("Cleaned up Chrome user data directory: " + userDataDir);
                }
            } catch (Exception e) {
                System.err.println("Failed to cleanup user data directory: " + e.getMessage());
            }
        }
    }
    
    private void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        directory.delete();
    }
}
```

### Step 3: Create Page Objects

**File:** `src/test/java/com/example/studentmonitor/ui/pages/LoginPage.java`

```java
package com.example.studentmonitor.ui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    @FindBy(id = "email")
    private WebElement emailField;
    
    @FindBy(id = "password")
    private WebElement passwordField;
    
    @FindBy(id = "loginBtn")
    private WebElement loginButton;
    
    @FindBy(id = "errorMessage")
    private WebElement errorMessage;
    
    @FindBy(linkText = "Register")
    private WebElement registerLink;

    public LoginPage(WebDriver driver, WebDriverWait wait) {
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

    public void clickLogin() {
        loginButton.click();
    }

    public String getErrorMessage() {
        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        return errorMessage.getText();
    }

    public void clickRegisterLink() {
        registerLink.click();
    }

    public boolean isLoginButtonDisplayed() {
        return loginButton.isDisplayed();
    }
}
```

### Step 3: Create Complete UI Test Class

**File:** `src/test/java/com/example/studentmonitor/ui/LoginUITest.java`

```java
package com.example.studentmonitor.ui;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginUITest extends BaseUITest {

    @Test
    void testNavigateToHomePage() {
        System.out.println("🔍 Running Home Page Navigation Test");
        
        // Navigate to home page
        driver.get(baseUrl);
        System.out.println("✅ Browser started - Testing URL: " + baseUrl);
        
        // Verify page loads
        String currentUrl = driver.getCurrentUrl();
        assertEquals(baseUrl + "/", currentUrl);
        System.out.println("✅ Navigation test passed - Current URL: " + currentUrl);
    }

    @Test
    void testHomePageTitle() {
        System.out.println("🔍 Running Home Page Title Test");
        
        // Navigate to home page
        driver.get(baseUrl);
        System.out.println("✅ Browser started - Testing URL: " + baseUrl);
        
        // Verify page title
        String title = driver.getTitle();
        assertEquals("Student Performance Monitor", title);
        System.out.println("✅ Home page loaded successfully with title: " + title);
    }

    @Test
    void testPageStructure() {
        System.out.println("🔍 Running Page Structure Test");
        
        // Navigate to home page
        driver.get(baseUrl);
        System.out.println("✅ Browser started - Testing URL: " + baseUrl);
        
        // Verify essential elements exist
        WebElement body = driver.findElement(By.tagName("body"));
        assertNotNull(body);
        System.out.println("✅ Page structure test passed");
    }
}
```

### Step 4: Execute UI Test 1 - Step by Step
```bash
# Run individual test methods
.\mvnw test -Dtest=LoginUITest#testNavigateToHomePage

# Expected output:
# - 🔍 Running Home Page Navigation Test
# - ✅ Browser started - Testing URL: http://localhost:xxxxx
# - ✅ Navigation test passed - Current URL: http://localhost:xxxxx/
# - BUILD SUCCESS

.\mvnw test -Dtest=LoginUITest#testHomePageTitle

# Expected output:
# - 🔍 Running Home Page Title Test  
# - ✅ Browser started - Testing URL: http://localhost:xxxxx
# - ✅ Home page loaded successfully with title: Student Performance Monitor
# - BUILD SUCCESS
```

### Step 3: Create Page Objects

**File:** `src/test/java/com/example/studentmonitor/ui/pages/RegistrationPage.java`

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

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
    
    @FindBy(id = "registerBtn")
    private WebElement registerButton;
    
    @FindBy(id = "successMessage")
    private WebElement successMessage;
    
    @FindBy(id = "errorMessage")
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

    public void clickRegister() {
        registerButton.click();
    }

    public String getSuccessMessage() {
        wait.until(ExpectedConditions.visibilityOf(successMessage));
        return successMessage.getText();
    }

    public String getErrorMessage() {
        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        return errorMessage.getText();
    }
}
```

### Step 4: Create UI Test Cases

**File:** `src/test/java/com/example/studentmonitor/ui/LoginUITest.java`

```java
package com.example.studentmonitor.ui;

import com.example.studentmonitor.ui.pages.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

public class LoginUITest extends BaseUITest {

    @Test
    @DisplayName("UI Test 1: Successful login with valid credentials")
    void testSuccessfulLogin() {
        // Arrange
        driver.get(baseUrl + "/login");
        LoginPage loginPage = new LoginPage(driver, wait);

        // Act
        loginPage.enterEmail("test@example.com");
        loginPage.enterPassword("password123");
        loginPage.clickLogin();

        // Assert
        // In a real app, you'd check for redirect to dashboard
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("dashboard") || currentUrl.contains("home"), 
                   "Should redirect to dashboard after successful login");
    }

    @Test
    @DisplayName("UI Test 2: Login fails with invalid credentials")
    void testLoginWithInvalidCredentials() {
        // Arrange
        driver.get(baseUrl + "/login");
        LoginPage loginPage = new LoginPage(driver, wait);

        // Act
        loginPage.enterEmail("invalid@example.com");
        loginPage.enterPassword("wrongpassword");
        loginPage.clickLogin();

        // Assert
        String errorMessage = loginPage.getErrorMessage();
        assertTrue(errorMessage.contains("Invalid") || errorMessage.contains("incorrect"), 
                   "Should show error message for invalid credentials");
    }

    @Test
    @DisplayName("Login page elements are displayed")
    void testLoginPageElements() {
        // Arrange
        driver.get(baseUrl + "/login");
        LoginPage loginPage = new LoginPage(driver, wait);

        // Assert
        assertTrue(loginPage.isLoginButtonDisplayed(), "Login button should be displayed");
    }
}
```

**File:** `src/test/java/com/example/studentmonitor/ui/RegistrationUITest.java`

```java
package com.example.studentmonitor.ui;

import com.example.studentmonitor.ui.pages.RegistrationPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

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
```

## Part 2: API Testing with REST Assured

### Step 1: Add REST Assured Dependencies

**File:** `pom.xml`

```xml
<dependencies>
    <!-- Existing dependencies... -->
    
    <!-- REST Assured Dependencies -->
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <version>5.3.2</version>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>json-schema-validator</artifactId>
        <version>5.3.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Step 2: Create API Test Base Class

**File:** `src/test/java/com/example/studentmonitor/api/BaseApiTest.java`

```java
package com.example.studentmonitor.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseApiTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}
```

### Step 3: Create API Test Cases

**File:** `src/test/java/com/example/studentmonitor/api/AuthApiTest.java`

```java
package com.example.studentmonitor.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class AuthApiTest extends BaseApiTest {

    @Test
    @Order(1)
    @DisplayName("API Test 1: POST /api/auth/signup - Successful registration")
    void testSuccessfulRegistration() {
        String requestBody = """
            {
                "email": "apitest@example.com",
                "password": "password123"
            }
            """;

        given()
            .contentType("application/json")
            .body(requestBody)
        .when()
            .post("/api/auth/signup")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("email", equalTo("apitest@example.com"))
            .body("password", not(equalTo("password123"))) // Should be encrypted
            .time(lessThan(2000L)); // Response time under 2 seconds
    }

    @Test
    @Order(2)
    @DisplayName("API Test 2: POST /api/auth/signup - Registration with existing email")
    void testRegistrationWithExistingEmail() {
        String requestBody = """
            {
                "email": "apitest@example.com",
                "password": "password123"
            }
            """;

        given()
            .contentType("application/json")
            .body(requestBody)
        .when()
            .post("/api/auth/signup")
        .then()
            .statusCode(400)
            .body("message", containsString("Email already taken"))
            .body("error", equalTo("Bad Request"));
    }

    @Test
    @DisplayName("API Test 3: POST /api/auth/signup - Invalid email format")
    void testRegistrationWithInvalidEmail() {
        String requestBody = """
            {
                "email": "invalid-email",
                "password": "password123"
            }
            """;

        given()
            .contentType("application/json")
            .body(requestBody)
        .when()
            .post("/api/auth/signup")
        .then()
            .statusCode(400)
            .body("message", containsString("valid email"));
    }

    @Test
    @DisplayName("API Test 4: POST /api/auth/signup - Weak password")
    void testRegistrationWithWeakPassword() {
        String requestBody = """
            {
                "email": "weakpass@example.com",
                "password": "123"
            }
            """;

        given()
            .contentType("application/json")
            .body(requestBody)
        .when()
            .post("/api/auth/signup")
        .then()
            .statusCode(400)
            .body("message", containsString("at least 6 characters"));
    }
}
```

**File:** `src/test/java/com/example/studentmonitor/api/StudentApiTest.java`

```java
package com.example.studentmonitor.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class StudentApiTest extends BaseApiTest {

    @Test
    @DisplayName("API Test 5: GET /api/students - Get all students")
    void testGetAllStudents() {
        given()
        .when()
            .get("/api/students")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(0)))
            .contentType("application/json");
    }

    @Test
    @DisplayName("API Test 6: POST /api/students - Create new student")
    void testCreateStudent() {
        String requestBody = """
            {
                "firstName": "John",
                "lastName": "Doe",
                "email": "john.doe@student.com",
                "dateOfBirth": "2000-01-01"
            }
            """;

        given()
            .contentType("application/json")
            .body(requestBody)
        .when()
            .post("/api/students")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("firstName", equalTo("John"))
            .body("lastName", equalTo("Doe"))
            .body("email", equalTo("john.doe@student.com"));
    }

    @Test
    @DisplayName("GET /api/students/{id} - Non-existent student")
    void testGetNonExistentStudent() {
        given()
        .when()
            .get("/api/students/999999")
        .then()
            .statusCode(404)
            .body("message", containsString("not found"));
    }
}
```

## Part 3: CI/CD Pipeline with GitHub Actions

## 🚀 **Step-by-Step CI/CD Pipeline Setup**

### Prerequisites
- GitHub repository created
- Project pushed to GitHub
- Java 17 and Maven configured
- Tests working locally

### **Step 1: Create GitHub Repository Structure**

**Execute these commands to prepare your repository:**
```bash
# Navigate to your project
cd c:\Users\usr\Desktop\Smon\Student_Monitoring_SB

# Initialize git if not already done
git init

# Add GitHub as remote origin
git remote add origin https://github.com/YOUR_USERNAME/Student_Monitoring_SB.git

# Create necessary directories
mkdir -p .github/workflows
```

### **Step 2: Create GitHub Actions Workflow File**

**Execute this step to create the main CI/CD pipeline:**

**Create File:** `.github/workflows/ci-cd.yml`

```yaml
name: Student Monitor CI/CD Pipeline

on:
  push:
    branches: [ main, master, develop ]
  pull_request:
    branches: [ main, master ]

env:
  JAVA_VERSION: '17'
  MAVEN_OPTS: '-Xmx1024m'

jobs:
  # Job 1: Unit and Integration Tests
  test:
    name: Run Tests
    runs-on: ubuntu-latest
    
    steps:
    - name: 📥 Checkout code
      uses: actions/checkout@v4

    - name: ☕ Set up JDK ${{ env.JAVA_VERSION }}
      uses: actions/setup-java@v4
      with:
        java-version: ${{ env.JAVA_VERSION }}
        distribution: 'temurin'

    - name: 📦 Cache Maven dependencies
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        restore-keys: ${{ runner.os }}-m2

    - name: 🧪 Run Unit Tests
      run: ./mvnw clean test -Dspring.profiles.active=ci -Dtest="*Test" -DexcludedGroups=integration,ui

    - name: 🔌 Run Integration Tests (API Tests)
      run: ./mvnw test -Dspring.profiles.active=ci -Dtest="**/*ApiTest"

    - name: 🥒 Run BDD Tests
      run: ./mvnw test -Dspring.profiles.active=ci -Dtest=CucumberTestRunner

    - name: 📊 Generate Test Reports
      run: ./mvnw surefire-report:report site:site

    - name: 📤 Upload Test Results
      uses: actions/upload-artifact@v4
      if: always()
      with:
        name: test-results
        path: |
          target/surefire-reports/
          target/site/
        retention-days: 30

  # Job 2: UI Tests (Selenium)
  ui-tests:
    name: Run UI Tests
    runs-on: ubuntu-latest
    needs: test
    
    steps:
    - name: 📥 Checkout code
      uses: actions/checkout@v4

    - name: ☕ Set up JDK ${{ env.JAVA_VERSION }}
      uses: actions/setup-java@v4
      with:
        java-version: ${{ env.JAVA_VERSION }}
        distribution: 'temurin'

    - name: 🌐 Set up Chrome Browser
      uses: browser-actions/setup-chrome@v1

    - name: 🚀 Start Application
      run: |
        # Build the application first
        ./mvnw clean package -DskipTests
        
        # Start application in background with test profile
        nohup java -jar target/*.jar --spring.profiles.active=test --server.port=8080 --management.endpoints.web.exposure.include=health,info > app.log 2>&1 &
        
        # Wait for application to start (increased timeout)
        echo "Waiting for application to start..."
        for i in {1..60}; do
          if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
            echo "✅ Application is ready after ${i} seconds"
            break
          fi
          if [ $i -eq 60 ]; then
            echo "❌ Application failed to start within 60 seconds"
            echo "Application logs:"
            cat app.log
            exit 1
          fi
          echo "Attempt ${i}/60: Waiting for application..."
          sleep 1
        done

    - name: 🖥️ Run UI Tests
      env:
        CI: true
        CHROME_BIN: /usr/bin/google-chrome
        DISPLAY: :99.0
      run: |
        # Start Xvfb for headless display
        sudo Xvfb :99 -screen 0 1920x1080x24 > /dev/null 2>&1 &
        sleep 3
        
        # Create unique temp directory for Chrome user data
        export CHROME_USER_DATA_DIR=$(mktemp -d)
        
        # Run UI tests with proper Chrome configuration
        ./mvnw test -Dtest="**/*UITest" \
          -Dspring.profiles.active=ci \
          -Dwebdriver.chrome.args="--user-data-dir=$CHROME_USER_DATA_DIR" \
          -Djava.awt.headless=true
        
        # Cleanup
        rm -rf "$CHROME_USER_DATA_DIR"

    - name: 📤 Upload UI Test Results
      uses: actions/upload-artifact@v4
      if: always()
      with:
        name: ui-test-results
        path: target/surefire-reports/
        retention-days: 30

  # Job 3: Security and Quality Checks
  security-quality:
    name: Security & Quality Checks
    runs-on: ubuntu-latest
    needs: test
    
    steps:
    - name: 📥 Checkout code
      uses: actions/checkout@v4

    - name: ☕ Set up JDK ${{ env.JAVA_VERSION }}
      uses: actions/setup-java@v4
      with:
        java-version: ${{ env.JAVA_VERSION }}
        distribution: 'temurin'

    - name: 🔍 Run OWASP Dependency Check
      run: ./mvnw org.owasp:dependency-check-maven:check

    - name: 📊 Generate Code Coverage
      run: ./mvnw clean test jacoco:report

    - name: 📤 Upload Security Reports
      uses: actions/upload-artifact@v4
      if: always()
      with:
        name: security-reports
        path: |
          target/dependency-check-report.html
          target/site/jacoco/
        retention-days: 30

  # Job 4: Build Application
  build:
    name: Build Application
    runs-on: ubuntu-latest
    needs: [test, ui-tests, security-quality]
    
    steps:
    - name: 📥 Checkout code
      uses: actions/checkout@v4

    - name: ☕ Set up JDK ${{ env.JAVA_VERSION }}
      uses: actions/setup-java@v4
      with:
        java-version: ${{ env.JAVA_VERSION }}
        distribution: 'temurin'

    - name: 🏗️ Build Application
      run: ./mvnw clean package -DskipTests

    - name: 📤 Upload Build Artifacts
      uses: actions/upload-artifact@v4
      with:
        name: jar-artifacts
        path: target/*.jar
        retention-days: 90

  # Job 5: Deploy to Staging
  deploy-staging:
    name: Deploy to Staging
    runs-on: ubuntu-latest
    needs: build
    if: github.ref == 'refs/heads/develop'
    environment: staging
    
    steps:
    - name: 📥 Download Build Artifacts
      uses: actions/download-artifact@v4
      with:
        name: jar-artifacts

    - name: 🚀 Deploy to Staging
      run: |
        echo "🚀 Deploying to Staging Environment..."
        echo "📦 Artifact: $(ls -la *.jar)"
        # Add your staging deployment commands here
        # Example: scp *.jar user@staging-server:/opt/app/

  # Job 6: Deploy to Production
  deploy-production:
    name: Deploy to Production
    runs-on: ubuntu-latest
    needs: build
    if: github.ref == 'refs/heads/main'
    environment: production
    
    steps:
    - name: 📥 Download Build Artifacts
      uses: actions/download-artifact@v4
      with:
        name: jar-artifacts

    - name: 🚀 Deploy to Production
      run: |
        echo "🚀 Deploying to Production Environment..."
        echo "📦 Artifact: $(ls -la *.jar)"
        # Add your production deployment commands here
```

### **Step 3: Create CI-Specific Test Configuration**

**Create File:** `src/test/resources/application-test.properties`

```properties
# CI/CD Test Configuration for Running App
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Enable web server for UI tests
spring.main.web-application-type=servlet
server.port=8080

# Actuator endpoints for health checks
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
management.endpoints.web.base-path=/actuator

# Logging configuration for CI
logging.level.com.example.studentmonitor=INFO
logging.level.org.springframework=WARN
logging.level.org.hibernate=WARN
logging.level.org.springframework.boot.actuate=INFO

# Fast startup for tests
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always

# Selenium CI Configuration
selenium.headless=true
selenium.timeout=60
selenium.page-load-timeout=30
selenium.implicit-wait=10

# Chrome driver settings for CI
webdriver.chrome.driver.silent=true
webdriver.chrome.verboseLogging=false
```

**Also create:** `src/test/resources/application-ci.properties`

```properties
# CI-specific configuration for unit tests only
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false

# Disable web server for unit tests
spring.main.web-application-type=none

# Minimal logging for unit tests
logging.level.root=WARN
logging.level.com.example.studentmonitor=WARN
```

### **Step-by-Step CI/CD Setup Procedure:**

#### **Phase 1: Repository Setup (5 minutes)**

**Step 1: Initialize Git Repository** (if not already done)
```bash
cd Student_Monitoring_SB
git init
git add .
git commit -m "Initial commit with complete test suite"
```

**Step 2: Create GitHub Repository**
   - Go to GitHub.com → New Repository
   - Name: `student-monitoring-system`
   - Description: `Spring Boot Student Monitoring with Complete Test Suite`
   - Choose Public/Private
   - Click "Create repository"

**Step 3: Connect Local to Remote**
```bash
git remote add origin https://github.com/YOUR_USERNAME/student-monitoring-system.git
git branch -M main
git push -u origin main
```

#### **Phase 2: Workflow Configuration (10 minutes)**

**Step 4: Create Workflow Directory**
```bash
mkdir -p .github/workflows
```

**Step 5: Add the CI/CD Workflow File**
   - Copy the complete workflow YAML above into `.github/workflows/ci-cd.yml`
   - This creates a comprehensive 6-job pipeline

**Step 6: Configure Environment-Specific Properties**
   - Create the `application-ci.properties` file above
   - This optimizes tests for CI environment

**Step 7: Commit Workflow Files**
```bash
git add .github/workflows/ci-cd.yml
git add src/test/resources/application-ci.properties
git commit -m "Add comprehensive CI/CD pipeline with 6-stage workflow"
git push origin main
```

#### **Phase 3: Pipeline Monitoring (2 minutes)**

**Step 8: Trigger First Pipeline Run**
   - Push triggers automatic pipeline execution
   - GitHub Actions starts within 10-30 seconds

**Step 9: Monitor Pipeline Execution**

**9.1: Access GitHub Actions Dashboard**
   - Go to GitHub repository → **Actions** tab
   - Click on the running workflow
   - You should see the pipeline status dashboard

**Expected Response - Pipeline Overview:**
```
📊 GitHub Actions Dashboard View:
┌─────────────────────────────────────────────────────────────┐
│ 🏃‍♂️ Student Monitor CI/CD Pipeline                          │
│ ⚡ Triggered by: push to main                              │
│ 🕐 Started: 1 minute ago                                   │
│ 👤 Commit: "Add comprehensive CI/CD pipeline"              │
│                                                             │
│ Pipeline Status: 🟡 IN PROGRESS                           │
│                                                             │
│ Jobs Progress:                                              │
│ ✅ test              (2m 30s) - COMPLETED                 │
│ 🟡 ui-tests          (1m 45s) - RUNNING                   │
│ ⏳ security-quality  - QUEUED (waiting for test)          │
│ ⏳ build             - QUEUED (waiting for dependencies)   │
│ ⏳ deploy-staging    - QUEUED (waiting for build)         │
│ ⏳ deploy-production - QUEUED (waiting for build)         │
└─────────────────────────────────────────────────────────────┘
```

**9.2: Click on Individual Jobs to See Detailed Logs**

**Expected Response - Test Job (✅ Completed Successfully):**
```
🧪 Run Tests - Execution Log:
┌─────────────────────────────────────────────────────────────┐
│ ✅ 📥 Checkout code                        (15s)           │
│ ✅ ☕ Set up JDK 17                       (45s)           │
│ ✅ 📦 Cache Maven dependencies            (12s)           │
│ ✅ 🧪 Run Unit Tests                      (35s)           │
│    [INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0│
│    [INFO] AuthServiceTest ................. PASSED        │
│    [INFO] StudentServiceTest .............. PASSED        │
│ ✅ 🔌 Run Integration Tests (API Tests)   (25s)           │
│    [INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0│
│    [INFO] AuthApiTest ..................... PASSED        │
│    [INFO] StudentApiTest .................. PASSED        │
│ ✅ 🥒 Run BDD Tests                       (40s)           │
│    [INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0│
│    [INFO] Cucumber scenarios: 7 passed, 0 failed         │
│ ✅ 📊 Generate Test Reports               (20s)           │
│ ✅ 📤 Upload Test Results                 (8s)            │
│                                                             │
│ 🎉 JOB COMPLETED SUCCESSFULLY - Total: 2m 30s            │
└─────────────────────────────────────────────────────────────┘
```

**Expected Response - UI Tests Job (🟡 Currently Running):**
```
🖥️ Run UI Tests - Live Execution Log:
┌─────────────────────────────────────────────────────────────┐
│ ✅ 📥 Checkout code                        (15s)           │
│ ✅ ☕ Set up JDK 17                       (45s)           │
│ ✅ 🌐 Set up Chrome Browser               (30s)           │
│    Chrome 118.0.5993.88 installed successfully            │
│ ✅ 🚀 Start Application                   (45s)           │
│    Application started on port 8080                        │
│    Health check: http://localhost:8080/actuator/health ✅  │
│ 🟡 🖥️ Run UI Tests                        (Running...)    │
│    [INFO] Running LoginUITest                              │
│    🔍 Running Home Page Navigation Test                    │
│    ✅ Browser started - Testing URL: http://localhost:8080 │
│    ✅ Navigation test passed                               │
│    🔍 Running Home Page Title Test                         │
│    ✅ Home page loaded with title: Student Performance... │
│    [INFO] Running RegistrationUITest                       │
│    🔍 Testing user registration flow...                    │
│                                                             │
│ 🟡 IN PROGRESS - Elapsed: 1m 45s                         │
└─────────────────────────────────────────────────────────────┘
```

**9.3: Watch Real-Time Progress Indicators**

**Expected Response - Complete Pipeline Timeline:**
```
📈 Pipeline Execution Timeline:
┌─────────────────────────────────────────────────────────────┐
│ 0:00 ──── ⚡ Pipeline Triggered                            │
│ 0:15 ──── 🧪 Test Job Started                             │
│ 2:45 ──── ✅ Test Job Completed (21 tests passed)         │
│ 2:50 ────┬── �️ UI Tests Started                          │
│          └── �🔍 Security & Quality Started                │
│ 6:35 ──── ✅ UI Tests Completed (3 UI tests passed)       │
│ 4:05 ──── ✅ Security & Quality Completed (0 vulnerabilities)│
│ 6:40 ──── 🏗️ Build Started                               │
│ 7:50 ──── ✅ Build Completed (JAR created: 15.2 MB)       │
│ 8:00 ──── 🚀 Deploy Production Started                    │
│ 8:45 ──── ✅ Deploy Production Completed                  │
│                                                             │
│ 🎉 PIPELINE SUCCESS - Total Duration: 8m 45s             │
└─────────────────────────────────────────────────────────────┘
```

**9.4: Final Success Response**

**Expected Response - Successful Pipeline Completion:**
```
🎉 Pipeline Completed Successfully!
┌─────────────────────────────────────────────────────────────┐
│ ✅ Student Monitor CI/CD Pipeline - SUCCESS                │
│ 🕐 Total Duration: 8m 45s                                  │
│ 📅 Completed: September 27, 2025 at 10:45 AM              │
│                                                             │
│ 📊 Test Results Summary:                                   │
│ • Unit Tests: 8/8 passed ✅                               │
│ • Integration Tests: 6/6 passed ✅                        │
│ • BDD Tests: 7/7 scenarios passed ✅                      │
│ • UI Tests: 3/3 passed ✅                                 │
│ • Security Scan: 0 vulnerabilities found ✅              │
│ • Code Coverage: 87.3% ✅                                 │
│                                                             │
│ 📦 Artifacts Generated:                                    │
│ • student-monitor-0.0.1-SNAPSHOT.jar (15.2 MB)           │
│ • Test Reports (HTML + XML)                               │
│ • Coverage Reports (JaCoCo)                               │
│ • Security Reports (OWASP)                                │
│                                                             │
│ 🚀 Deployment Status:                                     │
│ • Production: ✅ DEPLOYED (http://your-app.com)           │
│                                                             │
│ 🔗 Quick Links:                                           │
│ • View Test Reports                                        │
│ • Download Artifacts                                       │
│ • View Coverage Report                                     │
└─────────────────────────────────────────────────────────────┘
```

**9.5: Artifacts Available for Download**

**Expected Response - Artifacts Section:**
```
📦 Build Artifacts (Available for 90 days):
┌─────────────────────────────────────────────────────────────┐
│ 📄 test-results (5.2 MB)                                   │
│    └── Surefire reports, site documentation               │
│                                                             │
│ 📄 ui-test-results (1.8 MB)                               │
│    └── Selenium test execution logs                        │
│                                                             │
│ 📄 security-reports (2.1 MB)                              │
│    └── OWASP dependency check, JaCoCo coverage            │
│                                                             │
│ 📄 jar-artifacts (15.2 MB)                                │
│    └── student-monitor-0.0.1-SNAPSHOT.jar                 │
│                                                             │
│ 🔽 Download All | 📊 View Reports | 📈 View Metrics       │
└─────────────────────────────────────────────────────────────┘
```

**9.6: Email Notification Response**

**Expected Response - GitHub Email Notification:**
```
📧 Email: "GitHub Actions: Workflow run completed"
──────────────────────────────────────────────────────
From: GitHub Actions <noreply@github.com>
To: your-email@example.com
Subject: ✅ Student Monitor CI/CD Pipeline succeeded

✅ Your workflow run has completed successfully!

Repository: YOUR_USERNAME/student-monitoring-system
Workflow: Student Monitor CI/CD Pipeline
Branch: main
Commit: Add comprehensive CI/CD pipeline (abc123f)

Duration: 8m 45s
Jobs: 6/6 successful

View details: https://github.com/YOUR_USERNAME/repo/actions/runs/123456
──────────────────────────────────────────────────────
```

**Key Success Indicators to Look For:**
- ✅ All 6 jobs show green checkmarks
- ✅ Test counts: 24 total tests passed (8 unit + 6 API + 7 BDD + 3 UI)
- ✅ Build artifact created successfully
- ✅ No security vulnerabilities found
- ✅ Deployment completed without errors
- ✅ Total pipeline duration: 8-12 minutes

#### **Phase 4: Advanced Configuration (15 minutes)**

**Step 10: Configure Repository Secrets** (for production deployments)
```
# Go to: Repository Settings → Secrets and Variables → Actions
# Add these secrets:
```
- `STAGING_HOST`: staging server hostname
- `STAGING_USER`: deployment username
- `STAGING_SSH_KEY`: private SSH key
- `PROD_HOST`: production server hostname
- `PROD_USER`: production username
- `PROD_SSH_KEY`: production SSH key

**Step 11: Set Up Environment Protection Rules**
   - Repository Settings → Environments
   - Create `staging` environment
   - Create `production` environment
   - Add required reviewers for production

**Step 12: Configure Branch Protection Rules**
   - Settings → Branches
   - Add rule for `main` branch:
     - ✅ Require status checks to pass
     - ✅ Require branches to be up to date
     - ✅ Require pull request reviews

#### **Phase 5: Testing the Pipeline (5 minutes)**

**Step 13: Test Pipeline with Code Changes**
```bash
# Make a small change to trigger pipeline
echo "# CI/CD Pipeline Active" >> README.md
git add README.md
git commit -m "Test CI/CD pipeline trigger"
git push origin main
```

**Step 14: Verify All Pipeline Stages**
   - Monitor Actions tab for 5-10 minutes
   - Verify all 6 jobs complete successfully:
     - ✅ **test** (Unit, Integration, BDD)
     - ✅ **ui-tests** (Selenium)
     - ✅ **security-quality** (OWASP, Coverage)
     - ✅ **build** (JAR creation)
     - ✅ **deploy-staging** (if develop branch)
     - ✅ **deploy-production** (if main branch)

### **Expected Results:**
- ✅ **Complete Test Coverage**: 10+ tests (Unit, Integration, UI, BDD)
- ✅ **Automated Quality Gates**: Security scans, code coverage
- ✅ **Secure Deployments**: Environment-specific with approval workflows
- ✅ **Artifact Management**: Secure JAR storage and deployment
- ✅ **Real-time Monitoring**: Full pipeline visibility and reporting

### **Pipeline Success Indicators:**
```
✅ test (2m 30s) - Unit, Integration, BDD tests pass
✅ ui-tests (3m 45s) - Selenium tests complete
✅ security-quality (1m 20s) - No vulnerabilities found
✅ build (1m 10s) - JAR artifact created
✅ deploy-staging (45s) - Staging deployment successful
```

## Step 4: Run Commands

### Local Testing Commands

```bash
# Run all unit tests
./mvnw test

# Run API tests only
./mvnw test -Dtest="**/*ApiTest"

# Run UI tests only
./mvnw test -Dtest="**/*UITest"

# Run BDD tests
./mvnw test -Dtest=CucumberTestRunner

# Run with specific profile
./mvnw test -Dspring.profiles.active=test

# Generate test reports
./mvnw surefire-report:report

# Run tests with coverage
./mvnw clean test jacoco:report
```

### CI/CD Commands

```bash
# Simulate CI environment locally
export CI=true
./mvnw clean test

# Build for production
./mvnw clean package -Pproduction

# Run integration tests
./mvnw test -Dtest="**/*IntegrationTest"
```

## Screenshots to Take

1. **Selenium Tests Running:** Browser automation in action
2. **Test Reports:** Surefire HTML reports
3. **API Test Results:** REST Assured output
4. **GitHub Actions:** Pipeline execution
5. **Test Coverage:** JaCoCo coverage report

## Key Benefits Demonstrated

1. ✅ **Automated UI Testing** - Cross-browser compatibility
2. ✅ **API Testing** - Comprehensive endpoint validation
3. ✅ **CI/CD Integration** - Automated testing in pipeline
4. ✅ **Test Reporting** - Detailed test results and coverage
5. ✅ **Environment Management** - Different configs for different environments