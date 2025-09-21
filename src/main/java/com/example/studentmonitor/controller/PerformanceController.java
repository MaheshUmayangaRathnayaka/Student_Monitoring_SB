package com.example.studentmonitor.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.studentmonitor.dto.PerformanceDTO;
import com.example.studentmonitor.service.PerformanceService;

import jakarta.validation.Valid;

@RestController
public class PerformanceController {
    
    private final PerformanceService performanceService;
    
    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }
    
    @PostMapping("/api/students/{studentId}/performances")
    public ResponseEntity<PerformanceDTO> createPerformance(@PathVariable Long studentId, 
                                                           @Valid @RequestBody PerformanceDTO performanceDTO) {
        PerformanceDTO createdPerformance = performanceService.createPerformance(studentId, performanceDTO);
        return new ResponseEntity<>(createdPerformance, HttpStatus.CREATED);
    }
    
    @GetMapping("/api/students/{studentId}/performances")
    public ResponseEntity<List<PerformanceDTO>> getPerformancesByStudent(@PathVariable Long studentId) {
        List<PerformanceDTO> performances = performanceService.getByStudent(studentId);
        return ResponseEntity.ok(performances);
    }
    
    @GetMapping("/api/performances/{id}")
    public ResponseEntity<PerformanceDTO> getPerformance(@PathVariable Long id) {
        PerformanceDTO performance = performanceService.getPerformance(id);
        return ResponseEntity.ok(performance);
    }
    
    @PutMapping("/api/performances/{id}")
    public ResponseEntity<PerformanceDTO> updatePerformance(@PathVariable Long id, 
                                                           @Valid @RequestBody PerformanceDTO performanceDTO) {
        PerformanceDTO updatedPerformance = performanceService.updatePerformance(id, performanceDTO);
        return ResponseEntity.ok(updatedPerformance);
    }
    
    @DeleteMapping("/api/performances/{id}")
    public ResponseEntity<Void> deletePerformance(@PathVariable Long id) {
        performanceService.deletePerformance(id);
        return ResponseEntity.noContent().build();
    }
}