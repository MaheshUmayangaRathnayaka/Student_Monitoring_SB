package com.example.studentmonitor.service;

import com.example.studentmonitor.dto.SignupRequest;
import com.example.studentmonitor.model.User;

public interface AuthService {
    
    /**
     * Register a new user
     * @param signupRequest the signup request containing email and password
     * @return the created user
     * @throws IllegalArgumentException if email already exists
     */
    User signup(SignupRequest signupRequest);
}