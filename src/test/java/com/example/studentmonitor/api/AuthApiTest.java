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