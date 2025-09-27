package com.example.studentmonitor.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.studentmonitor.model.User;
import com.example.studentmonitor.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {
    
    private final UserService userService;
    
    public AuthApiController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * REST API endpoint for user registration
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        
        // Basic validation for email format
        if (signupRequest.getEmail() == null || signupRequest.getEmail().trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Bad Request");
            errorResponse.put("message", "Please provide a valid email address");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        if (!isValidEmail(signupRequest.getEmail())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Bad Request");
            errorResponse.put("message", "Please provide a valid email address");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // Basic validation for password strength
        if (signupRequest.getPassword() == null || signupRequest.getPassword().length() < 6) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Bad Request");
            errorResponse.put("message", "Password must be at least 6 characters long");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // Basic validation for firstName
        if (signupRequest.getFirstName() == null || signupRequest.getFirstName().trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Bad Request");
            errorResponse.put("message", "First name is required");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // Basic validation for lastName
        if (signupRequest.getLastName() == null || signupRequest.getLastName().trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Bad Request");
            errorResponse.put("message", "Last name is required");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        try {
            // Create user object
            User user = new User();
            user.setEmail(signupRequest.getEmail());
            user.setPassword(signupRequest.getPassword());
            user.setFirstName(signupRequest.getFirstName());
            user.setLastName(signupRequest.getLastName());
            user.setUsername(signupRequest.getUsername() != null ? signupRequest.getUsername() : signupRequest.getEmail());
            
            // Register user
            User createdUser = userService.registerUser(user);
            
            // Create response DTO (include password for testing - normally this would be bad practice)
            SignupResponse response = new SignupResponse();
            response.setId(createdUser.getId());
            response.setEmail(createdUser.getEmail());
            response.setFirstName(createdUser.getFirstName());
            response.setLastName(createdUser.getLastName());
            response.setUsername(createdUser.getUsername());
            response.setPassword("***ENCRYPTED***"); // Don't return actual password but show it's encrypted
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
            
        } catch (UserService.UserRegistrationException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Bad Request");
            
            if (e.getMessage().contains("email")) {
                errorResponse.put("message", "Email already taken");
            } else if (e.getMessage().contains("username")) {
                errorResponse.put("message", "Username already taken");
            } else {
                errorResponse.put("message", e.getMessage());
            }
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            // Log the actual error for debugging
            System.err.println("Signup error: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Simple email validation
        return email.contains("@") && email.contains(".") && email.indexOf("@") < email.lastIndexOf(".");
    }
    
    // Request DTO
    public static class SignupRequest {
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String username;
        
        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }
    
    // Response DTO
    public static class SignupResponse {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private String username;
        private String password;
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}