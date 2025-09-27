package com.example.studentmonitor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.studentmonitor.dto.SignupRequest;
import com.example.studentmonitor.model.User;
import com.example.studentmonitor.repository.UserRepository;
import com.example.studentmonitor.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public User signup(SignupRequest signupRequest) {
        // Validate required fields
        if (signupRequest.getEmail() == null || signupRequest.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        // Validate email format
        if (!isValidEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Please enter a valid email address");
        }
        
        // Validate password
        if (signupRequest.getPassword() == null || signupRequest.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        
        if (signupRequest.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email already taken");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(signupRequest.getPassword());
        
        // Use email as username for now
        user.setUsername(signupRequest.getEmail());
        
        // Set firstName and lastName from request, or use defaults
        user.setFirstName(signupRequest.getFirstName() != null ? signupRequest.getFirstName() : "User");
        user.setLastName(signupRequest.getLastName() != null ? signupRequest.getLastName() : "User");
        
        // Save and return the user
        return userRepository.save(user);
    }
    
    private boolean isValidEmail(String email) {
        // Simple email validation - contains @ and at least one dot after @
        return email != null && email.contains("@") && email.indexOf("@") < email.lastIndexOf(".");
    }
}