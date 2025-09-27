package com.example.studentmonitor.dto;

public class SignupRequest {
    
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    
    // No-arg constructor
    public SignupRequest() {
    }
    
    // Two-arg constructor for backward compatibility
    public SignupRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    // All-arg constructor
    public SignupRequest(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}