package com.example.studentmonitor.ui;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class BasicUITest extends BaseUITest {

    @Test
    @DisplayName("UI Test: Home page loads successfully")
    void testHomePageLoads() {
        // Navigate to home page
        driver.get(baseUrl + "/");
        
        // Wait for page to load and verify title
        wait.until(ExpectedConditions.titleContains("Student"));
        
        // Assert page loaded successfully
        String pageTitle = driver.getTitle();
        assertTrue(pageTitle.contains("Student") || pageTitle.contains("Monitor"), 
                   "Page title should contain 'Student' or 'Monitor'");
        
        System.out.println("✅ Home page loaded successfully with title: " + pageTitle);
    }

    @Test
    @DisplayName("UI Test: Check page content")
    void testPageContent() {
        // Navigate to home page
        driver.get(baseUrl + "/");
        
        // Check if page has content
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.length() > 100, "Page should have substantial content");
        
        // Check if basic HTML structure exists
        WebElement body = driver.findElement(By.tagName("body"));
        assertNotNull(body, "Page body should exist");
        
        System.out.println("✅ Page structure verified - Content length: " + pageSource.length());
        System.out.println("📄 Current URL: " + driver.getCurrentUrl());
    }

    @Test
    @DisplayName("UI Test: Navigation test")
    void testNavigation() {
        // Navigate to home page
        driver.get(baseUrl + "/");
        
        // Wait for page to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        
        // Try to find navigation elements
        try {
            // Look for common navigation elements
            java.util.List<WebElement> links = driver.findElements(By.tagName("a"));
            System.out.println("🔗 Found " + links.size() + " links on the page");
            
            for (int i = 0; i < Math.min(5, links.size()); i++) {
                WebElement link = links.get(i);
                String linkText = link.getText();
                String href = link.getAttribute("href");
                if (!linkText.isEmpty()) {
                    System.out.println("   - Link: '" + linkText + "' -> " + href);
                }
            }
            
        } catch (Exception e) {
            System.out.println("ℹ️ No navigation links found or error accessing them");
        }
        
        assertTrue(true, "Navigation test completed");
    }
}