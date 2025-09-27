package com.example.studentmonitor.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
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
        
        // Enable comprehensive logging for ALL requests and responses
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        
        // Also enable validation failure logging
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}