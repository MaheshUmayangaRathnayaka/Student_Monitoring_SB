# Test-Driven Development (TDD) Tutorial

## Overview
This tutorial demonstrates the Red-Green-Refactor cycle for TDD using Spring Boot and JUnit 5. Learn to write tests first, make them pass, and then refactor your code.

## Prerequisites
- Java 17+
- Spring Boot project (already created)
- Maven/Gradle
- IDE (IntelliJ IDEA/Eclipse)
- Terminal/Command Prompt

## 🎯 Step-by-Step Testing Instructions

### Quick Test Execution Commands

```bash
# Navigate to project directory
cd c:\Users\usr\Desktop\Smon\Student_Monitoring_SB

# Set JAVA_HOME (if not set)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"

# Run all TDD tests
.\mvnw test -Dtest=AuthServiceTest

# Run specific test method
.\mvnw test -Dtest=AuthServiceTest#testUserRegistrationSuccess
```

## TDD Cycle: Red-Green-Refactor

### Feature 1: User Registration (Signup)

#### Step 1: RED - Write Failing Test

**File:** `src/test/java/com/example/studentmonitor/service/AuthServiceTest.java`

```java
package com.example.studentmonitor.service;

import com.example.studentmonitor.dto.SignupRequest;
import com.example.studentmonitor.model.User;
import com.example.studentmonitor.repository.UserRepository;
import com.example.studentmonitor.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @InjectMocks AuthServiceImpl authService;

    @Test
    void signup_shouldSaveUser_whenEmailUnique() {
        // Arrange
        SignupRequest req = new SignupRequest("alice@example.com", "secret123");
        
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });

        // Act
        User result = authService.registerUser(req);

        // Assert
        assertNotNull(result);
        assertEquals("alice@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUserRegistrationFailure_EmailExists() {
        // Arrange
        SignupRequest req = new SignupRequest("existing@example.com", "secret123");
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authService.registerUser(req);
        });
        
        verify(userRepository, never()).save(any(User.class));
    }
}

    @Test
    void signup_shouldThrowException_whenEmailExists() {
        // Arrange
        SignupRequest req = new SignupRequest("bob@example.com", "password");
        when(userRepository.existsByEmail("bob@example.com")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.signup(req));
        assertEquals("Email already taken", ex.getMessage());
    }
}
```

**Command to run:**
```bash
./mvnw test -Dtest=AuthServiceTest
```

**Expected Result:** ❌ FAIL (UnsupportedOperationException)

#### Step 2: GREEN - Implement Minimum Code

**File:** `src/main/java/com/example/studentmonitor/dto/SignupRequest.java`

```java
package com.example.studentmonitor.dto;

public class SignupRequest {
    private String email;
    private String password;
    
    public SignupRequest() {}
    
    public SignupRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
```

**File:** `src/main/java/com/example/studentmonitor/service/AuthService.java`

```java
package com.example.studentmonitor.service;

import com.example.studentmonitor.dto.SignupRequest;
import com.example.studentmonitor.model.User;

public interface AuthService {
    User signup(SignupRequest signupRequest);
}
```

**File:** `src/main/java/com/example/studentmonitor/service/impl/AuthServiceImpl.java`

```java
package com.example.studentmonitor.service.impl;

import com.example.studentmonitor.dto.SignupRequest;
import com.example.studentmonitor.model.User;
import com.example.studentmonitor.repository.UserRepository;
import com.example.studentmonitor.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public User signup(SignupRequest signupRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email already taken");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(signupRequest.getPassword());
        user.setUsername(signupRequest.getEmail()); // Use email as username
        user.setFirstName("DefaultFirst");
        user.setLastName("DefaultLast");
        
        // Save and return
        return userRepository.save(user);
    }
}
```

**Command to run:**
```bash
./mvnw test -Dtest=AuthServiceTest
```

**Expected Result:** ✅ PASS

#### Step 3: REFACTOR - Improve Code Quality

**Enhanced AuthServiceImpl.java:**

```java
package com.example.studentmonitor.service.impl;

import com.example.studentmonitor.dto.SignupRequest;
import com.example.studentmonitor.model.User;
import com.example.studentmonitor.repository.UserRepository;
import com.example.studentmonitor.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public User signup(SignupRequest signupRequest) {
        validateSignupRequest(signupRequest);
        
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email already taken");
        }
        
        User user = createUserFromRequest(signupRequest);
        return userRepository.save(user);
    }
    
    private void validateSignupRequest(SignupRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }
    
    private User createUserFromRequest(SignupRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUsername(request.getEmail());
        user.setFirstName("DefaultFirst");
        user.setLastName("DefaultLast");
        return user;
    }
}
```

**Command to run:**
```bash
./mvnw test -Dtest=AuthServiceTest
```

**Expected Result:** ✅ PASS (All tests still pass after refactoring)

### Feature 2: Input Validation

#### Step 1: RED - Write Failing Test

**Add to AuthServiceTest.java:**

```java
@Test
void signup_shouldThrowException_whenEmailInvalid() {
    // Arrange
    SignupRequest req = new SignupRequest("", "password123");

    // Act & Assert
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> authService.signup(req));
    assertEquals("Email is required", ex.getMessage());
}

@Test
void signup_shouldThrowException_whenPasswordTooShort() {
    // Arrange
    SignupRequest req = new SignupRequest("test@example.com", "123");

    // Act & Assert
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> authService.signup(req));
    assertEquals("Password must be at least 6 characters", ex.getMessage());
}
```

#### Step 2: GREEN - Tests already pass due to refactored code!

#### Step 3: REFACTOR - Already done in previous refactor

## Screenshots to Take

1. **RED Phase:** Failed test results in IDE
2. **GREEN Phase:** Passing test results after implementation
3. **REFACTOR Phase:** Clean code with all tests still passing
4. **Test Coverage Report:** Run `./mvnw jacoco:report` and show coverage

## Commands Summary

```bash
# Run specific test
./mvnw test -Dtest=AuthServiceTest

# Run all tests
./mvnw test

# Run tests with coverage
./mvnw clean test jacoco:report

# View coverage report
# Open target/site/jacoco/index.html in browser
```

## Key TDD Principles Demonstrated

1. ✅ **Write test first** - Tests written before implementation
2. ✅ **Red-Green-Refactor** - Clear cycle demonstrated
3. ✅ **Minimum code** - Only enough code to pass tests
4. ✅ **Refactoring** - Improved code quality while keeping tests green
5. ✅ **Fast feedback** - Quick test execution and results