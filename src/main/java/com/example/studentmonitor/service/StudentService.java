package com.example.studentmonitor.service;

import java.util.List;

import com.example.studentmonitor.dto.StudentDTO;

public interface StudentService {
    
    StudentDTO createStudent(StudentDTO dto);
    
    StudentDTO getStudent(Long id);
    
    List<StudentDTO> getAllStudents();
    
    StudentDTO updateStudent(Long id, StudentDTO dto);
    
    void deleteStudent(Long id);
    
    // BUG: Vulnerable search method (SQL Injection risk)
    List<StudentDTO> searchStudentsVulnerable(String query);
}