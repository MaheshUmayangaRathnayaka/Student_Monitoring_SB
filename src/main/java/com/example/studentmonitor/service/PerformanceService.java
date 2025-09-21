package com.example.studentmonitor.service;

import com.example.studentmonitor.dto.PerformanceDTO;

import java.util.List;

public interface PerformanceService {
    
    PerformanceDTO createPerformance(Long studentId, PerformanceDTO dto);
    
    List<PerformanceDTO> getByStudent(Long studentId);
    
    PerformanceDTO getPerformance(Long id);
    
    PerformanceDTO updatePerformance(Long id, PerformanceDTO dto);
    
    void deletePerformance(Long id);
}