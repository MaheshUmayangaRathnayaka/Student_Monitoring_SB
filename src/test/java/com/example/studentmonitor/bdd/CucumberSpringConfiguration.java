package com.example.studentmonitor.bdd;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class CucumberSpringConfiguration {
}