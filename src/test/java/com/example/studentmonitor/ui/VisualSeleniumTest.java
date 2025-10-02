package com.example.studentmonitor.ui;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class VisualSeleniumTest extends BaseUITest {

    @Test
    @DisplayName("🎬 Visual Test: Watch Selenium in Action")
    void testSeleniumVisualExecution() throws InterruptedException {
        System.out.println("🎬 Starting Visual Selenium Test - Watch your browser!");
        
        // Navigate to home page
        System.out.println("🌐 Step 1: Navigating to home page...");
        driver.get(baseUrl + "/");
        Thread.sleep(2000); // Pause to see navigation
        
        // Wait for page to load and verify
        System.out.println("⏳ Step 2: Waiting for page to load...");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        Thread.sleep(1000);
        
        // Get and verify page title
        System.out.println("📋 Step 3: Checking page title...");
        String pageTitle = driver.getTitle();
        System.out.println("✅ Page Title: " + pageTitle);
        assertNotNull(pageTitle, "Page title should not be null");
        Thread.sleep(1000);
        
        // Check page content
        System.out.println("🔍 Step 4: Verifying page content...");
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.length() > 100, "Page should have content");
        System.out.println("✅ Page has " + pageSource.length() + " characters of content");
        Thread.sleep(1000);
        
        // Verify current URL
        System.out.println("🔗 Step 5: Verifying current URL...");
        String currentUrl = driver.getCurrentUrl();
        System.out.println("✅ Current URL: " + currentUrl);
        assertTrue(currentUrl.contains("localhost"), "Should be on localhost");
        Thread.sleep(1000);
        
        // Check for specific elements
        System.out.println("🎯 Step 6: Looking for page elements...");
        WebElement body = driver.findElement(By.tagName("body"));
        assertNotNull(body, "Page should have a body element");
        System.out.println("✅ Found body element");
        Thread.sleep(1000);
        
        WebElement htmlElement = driver.findElement(By.tagName("html"));
        assertNotNull(htmlElement, "Page should have an html element");
        System.out.println("✅ Found html element");
        Thread.sleep(2000);
        
        System.out.println("🎉 Visual test completed successfully!");
        System.out.println("📊 Test Summary:");
        System.out.println("   - Page Title: " + pageTitle);
        System.out.println("   - URL: " + currentUrl);
        System.out.println("   - Content Length: " + pageSource.length() + " chars");
        System.out.println("   - Elements Found: body, html");
    }
    
    @Test
    @DisplayName("🔄 Test with Screenshots")
    void testWithScreenshotCapability() throws InterruptedException {
        System.out.println("📸 Starting test with screenshot capability...");
        
        // Navigate and take action
        driver.get(baseUrl + "/");
        Thread.sleep(1000);
        
        // You could add screenshot capability here if needed
        String currentUrl = driver.getCurrentUrl();
        System.out.println("📍 Current location: " + currentUrl);
        
        // Verify page loaded
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        System.out.println("✅ Page verification complete");
        
        Thread.sleep(2000); // Allow time to observe
        System.out.println("📸 Screenshot point - browser should be visible");
    }
}