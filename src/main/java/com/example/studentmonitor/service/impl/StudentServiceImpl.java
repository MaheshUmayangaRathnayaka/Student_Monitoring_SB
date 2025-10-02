package com.example.studentmonitor.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.studentmonitor.dto.StudentDTO;
import com.example.studentmonitor.exception.ResourceNotFoundException;
import com.example.studentmonitor.model.Student;
import com.example.studentmonitor.repository.StudentRepository;
import com.example.studentmonitor.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {
    
    private final StudentRepository studentRepository;
    
    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    
    @Override
    public StudentDTO createStudent(StudentDTO dto) {
        // BUG INTRODUCED: Race condition in student creation
        // Check if email exists, but there's a gap before saving
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Student with email " + dto.getEmail() + " already exists");
        }
        
        // RACE CONDITION: Another thread could create student with same email here
        // Simulate some processing delay that makes race condition more likely
        try {
            Thread.sleep(100); // 100ms delay - creates window for race condition
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // By now, another thread might have created student with same email
        Student student = mapToEntity(dto);
        Student savedStudent = studentRepository.save(student); // Could fail with duplicate email
        return mapToDto(savedStudent);
    }
    
    @Override
    public StudentDTO getStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return mapToDto(student);
    }
    
    @Override
    public List<StudentDTO> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return students.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public StudentDTO updateStudent(Long id, StudentDTO dto) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        
        existingStudent.setFirstName(dto.getFirstName());
        existingStudent.setLastName(dto.getLastName());
        existingStudent.setEmail(dto.getEmail());
        existingStudent.setDateOfBirth(dto.getDateOfBirth());
        
        Student updatedStudent = studentRepository.save(existingStudent);
        return mapToDto(updatedStudent);
    }
    
    @Override
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        studentRepository.delete(student);
    }
    
    // Manual mapping methods
    private StudentDTO mapToDto(Student student) {
        return new StudentDTO(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getDateOfBirth()
        );
    }
    
    private Student mapToEntity(StudentDTO dto) {
        Student student = new Student();
        student.setId(dto.getId());
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setDateOfBirth(dto.getDateOfBirth());
        return student;
    }
    
    // BUG INTRODUCED: SQL Injection Vulnerability
    @Override
    public List<StudentDTO> searchStudentsVulnerable(String query) {
        // CRITICAL SECURITY FLAW: Direct string concatenation in SQL
        // This allows SQL injection attacks like: test'; DROP TABLE students; --
        
        // Simulate vulnerable query execution (this would normally use EntityManager.createNativeQuery)
        System.out.println("EXECUTING VULNERABLE SQL: SELECT * FROM students WHERE first_name LIKE '%" + query + "%'");
        
        // For demo purposes, log the dangerous query that would be executed
        if (query.contains("'") || query.contains(";") || query.toLowerCase().contains("drop") 
            || query.toLowerCase().contains("delete") || query.toLowerCase().contains("update")) {
            System.err.println("⚠️ SECURITY ALERT: Potential SQL injection detected in query: " + query);
            System.err.println("⚠️ In a real system, this could: DROP TABLES, DELETE DATA, STEAL INFORMATION!");
        }
        
        // Return filtered results (safe fallback for demo)
        return getAllStudents().stream()
                .filter(student -> 
                    student.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                    student.getLastName().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }
}