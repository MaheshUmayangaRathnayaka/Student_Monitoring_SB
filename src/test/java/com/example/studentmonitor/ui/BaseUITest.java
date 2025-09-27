package com.example.studentmonitor.ui;

import java.io.File;
import java.time.Duration;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.github.bonigarcia.wdm.WebDriverManager;

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
        String headless = System.getProperty("headless", "false");
        boolean isCIEnvironment = "true".equals(isCI);
        boolean isHeadless = "true".equals(headless) || isCIEnvironment;
        
        if (isHeadless) {
            options.addArguments("--headless=new");  // Use new headless mode
            System.out.println("🤖 Running in headless mode");
        } else {
            System.out.println("🌐 Running with visible browser");
        }
        
        // Essential Chrome arguments
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-web-security");
        options.addArguments("--window-size=1920,1080");
        
        if (isCIEnvironment) {
            // Additional CI-specific arguments
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
            options.addArguments("--remote-debugging-port=9222");
            
            // Set additional preferences for CI
            options.setExperimentalOption("useAutomationExtension", false);
            options.setExperimentalOption("excludeSwitches", 
                java.util.Arrays.asList("enable-automation"));
        }
        
        // CRITICAL FIX: Create unique user data directory using UUID
        userDataDir = createUniqueUserDataDir();
        options.addArguments("--user-data-dir=" + userDataDir);
        
        // Common settings to prevent automation detection
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-features=VizDisplayCompositor");
        
        try {
            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            baseUrl = "http://localhost:" + port;
            
            // Set timeouts
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            
            System.out.println("✅ ChromeDriver initialized successfully");
            System.out.println("🚀 Browser started - Testing URL: " + baseUrl);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize ChromeDriver: " + e.getMessage());
            cleanupUserDataDir();
            throw e;
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("✅ ChromeDriver closed successfully");
            } catch (Exception e) {
                System.err.println("⚠️ Error closing driver: " + e.getMessage());
            }
        }
        
        // Clean up user data directory
        cleanupUserDataDir();
    }
    
    /**
     * Creates a unique Chrome user data directory using UUID to prevent conflicts
     * @return Absolute path to the unique directory
     */
    private String createUniqueUserDataDir() {
        String tempDir = System.getProperty("java.io.tmpdir");
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String threadId = String.valueOf(Thread.currentThread().getId());
        
        String uniqueDir = tempDir + File.separator + 
                          "chrome-profile-" + uniqueId + "-" + timestamp + "-" + threadId;
        
        File dir = new File(uniqueDir);
        if (!dir.mkdirs() && !dir.exists()) {
            throw new RuntimeException("Failed to create Chrome user data directory: " + uniqueDir);
        }
        
        System.out.println("📁 Created Chrome user data directory: " + uniqueDir);
        return uniqueDir;
    }
    
    /**
     * Cleanup the Chrome user data directory after test completion
     */
    private void cleanupUserDataDir() {
        if (userDataDir != null) {
            try {
                File dir = new File(userDataDir);
                if (dir.exists()) {
                    deleteDirectory(dir);
                    System.out.println("🧹 Cleaned up Chrome user data directory: " + userDataDir);
                }
            } catch (Exception e) {
                System.err.println("⚠️ Failed to cleanup user data directory: " + e.getMessage());
                // Don't throw exception here as it's cleanup code
            }
        }
    }
    
    /**
     * Recursively delete a directory and all its contents
     * @param directory Directory to delete
     */
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