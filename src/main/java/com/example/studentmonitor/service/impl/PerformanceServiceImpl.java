package com.example.studentmonitor.service.impl;

import com.example.studentmonitor.dto.PerformanceDTO;
import com.example.studentmonitor.exception.ResourceNotFoundException;
import com.example.studentmonitor.model.PerformanceRecord;
import com.example.studentmonitor.model.Student;
import com.example.studentmonitor.repository.PerformanceRepository;
import com.example.studentmonitor.repository.StudentRepository;
import com.example.studentmonitor.service.PerformanceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PerformanceServiceImpl implements PerformanceService {
    
    private final PerformanceRepository performanceRepository;
    private final StudentRepository studentRepository;
    
    public PerformanceServiceImpl(PerformanceRepository performanceRepository, StudentRepository studentRepository) {
        this.performanceRepository = performanceRepository;
        this.studentRepository = studentRepository;
    }
    
    @Override
    public PerformanceDTO createPerformance(Long studentId, PerformanceDTO dto) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        
        PerformanceRecord performance = mapToEntity(dto);
        performance.setStudent(student);
        
        PerformanceRecord savedPerformance = performanceRepository.save(performance);
        return mapToDto(savedPerformance);
    }
    
    @Override
    public List<PerformanceDTO> getByStudent(Long studentId) {
        // Verify student exists
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        
        List<PerformanceRecord> performances = performanceRepository.findByStudentId(studentId);
        return performances.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public PerformanceDTO getPerformance(Long id) {
        PerformanceRecord performance = performanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Performance record not found with id: " + id));
        return mapToDto(performance);
    }
    
    @Override
    public PerformanceDTO updatePerformance(Long id, PerformanceDTO dto) {
        PerformanceRecord existingPerformance = performanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Performance record not found with id: " + id));
        
        existingPerformance.setSubject(dto.getSubject());
        existingPerformance.setScore(dto.getScore());
        existingPerformance.setDate(dto.getDate());
        existingPerformance.setRemarks(dto.getRemarks());
        
        PerformanceRecord updatedPerformance = performanceRepository.save(existingPerformance);
        return mapToDto(updatedPerformance);
    }
    
    @Override
    public void deletePerformance(Long id) {
        PerformanceRecord performance = performanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Performance record not found with id: " + id));
        performanceRepository.delete(performance);
    }
    
    // Manual mapping methods
    private PerformanceDTO mapToDto(PerformanceRecord performance) {
        return new PerformanceDTO(
                performance.getId(),
                performance.getSubject(),
                performance.getScore(),
                performance.getDate(),
                performance.getRemarks(),
                performance.getStudent() != null ? performance.getStudent().getId() : null
        );
    }
    
    private PerformanceRecord mapToEntity(PerformanceDTO dto) {
        PerformanceRecord performance = new PerformanceRecord();
        performance.setId(dto.getId());
        performance.setSubject(dto.getSubject());
        performance.setScore(dto.getScore());
        performance.setDate(dto.getDate());
        performance.setRemarks(dto.getRemarks());
        return performance;
    }
    
    // BUG INTRODUCED: Null Pointer Exception
    public Double calculateAverageScore(Long studentId) {
        List<PerformanceRecord> performances = performanceRepository.findByStudentId(studentId);
        
        if (performances.isEmpty()) {
            // BUG: This will cause NPE when trying to access .doubleValue()
            Double average = null;
            System.out.println("Calculating average for student with no records...");
            return average.doubleValue(); // CRASH: NullPointerException here!
        }
        
        return performances.stream()
                .mapToDouble(PerformanceRecord::getScore)
                .average()
                .orElse(0.0);
    }
}