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
        Student student = mapToEntity(dto);
        Student savedStudent = studentRepository.save(student);
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
}