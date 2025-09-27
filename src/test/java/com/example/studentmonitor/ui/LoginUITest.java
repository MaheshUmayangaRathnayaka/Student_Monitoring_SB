package com.example.studentmonitor.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

public class LoginUITest extends BaseUITest {

    @Test
    @DisplayName("UI Test 1: Home page loads successfully")
    void testHomePageLoads() {
        // Navigate to home page
        driver.get(baseUrl + "/");
        
        // Wait for page to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        
        // Assert page loaded successfully
        String pageTitle = driver.getTitle();
        assertNotNull(pageTitle, "Page title should not be null");
        
        // Check page content exists
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.length() > 100, "Page should have content");
        
        System.out.println("✅ Home page loaded successfully with title: " + pageTitle);
    }

    @Test
    @DisplayName("UI Test 2: Page navigation works")
    void testPageNavigation() {
        // Navigate to home page
        driver.get(baseUrl + "/");
        
        // Wait for page to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        
        // Get current URL
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("localhost"), "Should be on localhost");
        
        System.out.println("✅ Navigation test passed - Current URL: " + currentUrl);
    }

    @Test
    @DisplayName("UI Test 3: Basic page structure")
    void testPageStructure() {
        // Navigate to home page
        driver.get(baseUrl + "/");
        
        // Wait for page to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        
        // Check basic HTML structure
        WebElement body = driver.findElement(By.tagName("body"));
        assertNotNull(body, "Page should have a body element");
        
        WebElement htmlElement = driver.findElement(By.tagName("html"));
        assertNotNull(htmlElement, "Page should have an html element");
        
        System.out.println("✅ Page structure test passed");
    }
}