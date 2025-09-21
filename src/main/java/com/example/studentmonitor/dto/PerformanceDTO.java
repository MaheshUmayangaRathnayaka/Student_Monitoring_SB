package com.example.studentmonitor.dto;

import java.time.LocalDate;

public class PerformanceDTO {
    
    private Long id;
    private String subject;
    private Double score;
    private LocalDate date;
    private String remarks;
    private Long studentId;
    
    // No-arg constructor
    public PerformanceDTO() {
    }
    
    // All-arg constructor
    public PerformanceDTO(Long id, String subject, Double score, LocalDate date, String remarks, Long studentId) {
        this.id = id;
        this.subject = subject;
        this.score = score;
        this.date = date;
        this.remarks = remarks;
        this.studentId = studentId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public Double getScore() {
        return score;
    }
    
    public void setScore(Double score) {
        this.score = score;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public Long getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
}