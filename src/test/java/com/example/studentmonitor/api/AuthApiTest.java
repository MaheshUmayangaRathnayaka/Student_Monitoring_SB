package com.example.studentmonitor.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthApiTest extends BaseApiTest {

    @Test
    @Order(1)
    @DisplayName("API Test 1: POST /api/auth/signup - Successful registration")
    void testSuccessfulRegistration() {
        String requestBody = """
            {
                "email": "test1@example.com",
                "password": "password123",
                "firstName": "Test",
                "lastName": "One"
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
            .body("email", equalTo("test1@example.com"))
            .body("password", not(equalTo("password123"))) // Should be encrypted
            .time(lessThan(2000L)); // Response time under 2 seconds
    }

    @Test
    @Order(2)  
    @DisplayName("API Test 2: POST /api/auth/signup - Registration with existing email")
    void testRegistrationWithExistingEmail() {
        // First, create a user
        String firstRequestBody = """
            {
                "email": "dup@test.com",
                "password": "password123",
                "firstName": "Duplicate",
                "lastName": "User"
            }
            """;

        given()
            .contentType("application/json")
            .body(firstRequestBody)
        .when()
            .post("/api/auth/signup")
        .then()
            .statusCode(201);

        // Then try to create the same user again
        String secondRequestBody = """
            {
                "email": "dup@test.com",
                "password": "password123",
                "firstName": "Duplicate",
                "lastName": "User"
            }
            """;

        given()
            .contentType("application/json")
            .body(secondRequestBody)
        .when()
            .post("/api/auth/signup")
        .then()
            .statusCode(400)
            .body("message", equalTo("Email is already taken!"))
            .body("error", equalTo("Bad Request"));
    }

    @Test
    @DisplayName("API Test 3: POST /api/auth/signup - Invalid email format")
    void testRegistrationWithInvalidEmail() {
        String requestBody = """
            {
                "email": "invalid-email",
                "password": "password123",
                "firstName": "Invalid",
                "lastName": "Email"
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
                "password": "123",
                "firstName": "Weak",
                "lastName": "Password"
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