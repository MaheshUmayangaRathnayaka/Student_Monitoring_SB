# 🚀 Complete Guide: Running Selenium Tests Locally

## Overview
This guide covers multiple ways to run Selenium UI tests locally in your Student Monitoring Spring Boot application.

## Prerequisites
- ✅ Java 17 installed
- ✅ Maven 3.6+ 
- ✅ Chrome browser installed
- ✅ Spring Boot application with test dependencies

---

## 🎯 Method 1: Maven Command Line

### Run All Selenium Tests
```bash
# Set JAVA_HOME and run all UI tests
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
.\mvnw test -Dtest="*UITest"
```

### Run Specific Test Class
```bash
# Run only LoginUITest
.\mvnw test -Dtest=LoginUITest

# Run BasicUITest
.\mvnw test -Dtest=BasicUITest
```

### Run Single Test Method
```bash
# Run specific test method
.\mvnw test -Dtest=LoginUITest#testHomePageLoads
```

---

## 🌐 Method 2: Browser Visibility Options

### Visible Browser Mode (Default)
```bash
# Run with visible Chrome browser
.\mvnw test -Dtest=LoginUITest
```

### Headless Mode
```bash
# Run in headless mode (no visible browser)
.\mvnw test -Dtest=LoginUITest -Dheadless=true
```

### Custom Window Size
```bash
# Run with custom browser size
.\mvnw test -Dtest=LoginUITest -Dwindow.width=1366 -Dwindow.height=768
```

---

## 🔧 Method 3: IDE Integration

### IntelliJ IDEA
1. **Right-click test class** → Run 'LoginUITest'
2. **Right-click test method** → Run specific test
3. **Use Run Configuration**:
   - Go to Run → Edit Configurations
   - Add VM options: `-Dheadless=false`
   - Set Environment variables: `JAVA_HOME=C:\Program Files\Java\jdk-17`

### VS Code
1. Install **Extension Pack for Java**
2. Install **Test Runner for Java**
3. Click **▶️ Run Test** button next to test methods
4. Use Command Palette: `Java: Run Tests`

---

## 🎛️ Method 4: Custom Test Profiles

### Create Test Profiles in application-test.properties
```properties
# src/test/resources/application-test.properties
spring.profiles.active=test
server.port=0
selenium.headless=false
selenium.timeout=30
selenium.window.width=1920
selenium.window.height=1080
```

### Run with Profile
```bash
.\mvnw test -Dtest=LoginUITest -Dspring.profiles.active=test
```

---

## 🐛 Method 5: Debug Mode

### Debug with Breakpoints
```bash
# Run in debug mode with suspend
.\mvnw test -Dtest=LoginUITest -Dmaven.surefire.debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
```

### Slow Motion Testing
```java
// Add delays for debugging
Thread.sleep(2000); // 2 second pause
```

---

## 📊 Method 6: Parallel Execution

### Run Tests in Parallel
```bash
# Run multiple test classes in parallel
.\mvnw test -Dtest="LoginUITest,BasicUITest" -DforkCount=2 -DreuseForks=false
```

### Configure Parallel in pom.xml
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <parallel>methods</parallel>
        <threadCount>3</threadCount>
    </configuration>
</plugin>
```

---

## 🎨 Method 7: Different Browser Options

### Chrome Options Configuration
```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--start-maximized");
options.addArguments("--incognito");
options.addArguments("--disable-extensions");
options.addArguments("--disable-popup-blocking");
```

### Firefox Alternative
```bash
# Switch to Firefox (requires WebDriverManager setup)
.\mvnw test -Dtest=LoginUITest -Dbrowser=firefox
```

---

## 📝 Method 8: Test Reporting

### Generate HTML Reports
```bash
# Run tests with Surefire reports
.\mvnw test -Dtest="*UITest" surefire-report:report
```

### Screenshots on Failure
```java
// Add to BaseUITest for screenshot capture
@AfterEach
void captureScreenshotOnFailure(TestInfo testInfo) {
    if (/* test failed */) {
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        byte[] screenshotBytes = screenshot.getScreenshotAs(OutputType.BYTES);
        // Save screenshot
    }
}
```

---

## 🚦 Method 9: Environment-Specific Testing

### Local Development
```bash
# Run against local server
.\mvnw test -Dtest=LoginUITest -Dserver.port=8080
```

### Test Against Different Environments
```bash
# Test against staging
.\mvnw test -Dtest=LoginUITest -Dbase.url=http://staging.example.com

# Test against production
.\mvnw test -Dtest=LoginUITest -Dbase.url=https://prod.example.com
```

---

## 🔍 Method 10: Test Filtering & Tags

### Run Tests by Category
```java
@Tag("ui")
@Tag("smoke")
public class LoginUITest extends BaseUITest {
    // test methods
}
```

```bash
# Run only smoke tests
.\mvnw test -Dgroups="smoke"

# Run UI tests only
.\mvnw test -Dgroups="ui"
```

---

## 🛠️ Troubleshooting Common Issues

### Issue 1: JAVA_HOME Not Set
```bash
# Solution: Set JAVA_HOME before running
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
```

### Issue 2: Chrome Driver Version Mismatch
```bash
# Solution: Update WebDriverManager
.\mvnw dependency:tree | findstr webdrivermanager
```

### Issue 3: Port Already in Use
```bash
# Solution: Use random port
.\mvnw test -Dserver.port=0 -Dtest=LoginUITest
```

### Issue 4: Tests Running Too Fast
```java
// Solution: Add explicit waits
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.elementToBeClickable(By.id("login-button")));
```

---

## 📋 Quick Reference Commands

| Command | Description |
|---------|-------------|
| `.\mvnw test -Dtest=LoginUITest` | Run single test class |
| `.\mvnw test -Dtest="*UITest"` | Run all UI tests |
| `.\mvnw test -Dheadless=true` | Run in headless mode |
| `.\mvnw test -DforkCount=2` | Run tests in parallel |
| `.\mvnw test -Dgroups="smoke"` | Run tagged tests only |

---

## 🎯 Best Practices

1. **Always use explicit waits** instead of Thread.sleep()
2. **Run in headless mode for CI/CD** pipelines
3. **Use visible browser for debugging** locally
4. **Keep tests independent** - each test should be able to run alone
5. **Clean up resources** in @AfterEach methods
6. **Use Page Object Model** for complex applications
7. **Take screenshots on failures** for debugging
8. **Use meaningful test names** and descriptions

---

## 🚀 Next Steps

- Set up CI/CD pipeline integration
- Add cross-browser testing
- Implement screenshot capture
- Create custom test reports
- Add performance testing metrics

---

*Last updated: September 26, 2025*