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