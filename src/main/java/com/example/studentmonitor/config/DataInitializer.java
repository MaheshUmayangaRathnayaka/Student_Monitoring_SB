package com.example.studentmonitor.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.studentmonitor.model.PerformanceRecord;
import com.example.studentmonitor.model.Student;
import com.example.studentmonitor.repository.PerformanceRepository;
import com.example.studentmonitor.repository.StudentRepository;
import com.example.studentmonitor.service.UserService;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final PerformanceRepository performanceRepository;
    private final UserService userService;
    
    // BUG INTRODUCED: Memory leak - static list that grows indefinitely
    private static final List<String> INITIALIZATION_LOG = new ArrayList<>();

    public DataInitializer(StudentRepository studentRepository, 
                          PerformanceRepository performanceRepository,
                          UserService userService) {
        this.studentRepository = studentRepository;
        this.performanceRepository = performanceRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create default admin user first
        createDefaultUsers();
        
        // Check if student data already exists
        if (studentRepository.count() > 0) {
            return; // Student data already initialized
        }

        // Create sample students and performance data
        createSampleData();
    }
    
    private void createDefaultUsers() {
        try {
            userService.createAdminIfNotExists();
            System.out.println("Default admin user created successfully!");
        } catch (Exception e) {
            System.err.println("Error creating default admin user: " + e.getMessage());
        }
    }
    
    private void createSampleData() {

        // Create sample students
        Student student1 = new Student();
        student1.setFirstName("John");
        student1.setLastName("Doe");
        student1.setEmail("john.doe@example.com");
        student1.setDateOfBirth(LocalDate.of(2000, 5, 15));

        Student student2 = new Student();
        student2.setFirstName("Jane");
        student2.setLastName("Smith");
        student2.setEmail("jane.smith@example.com");
        student2.setDateOfBirth(LocalDate.of(1999, 8, 22));

        // Save students
        student1 = studentRepository.save(student1);
        student2 = studentRepository.save(student2);

        // Create sample performance records for student1
        PerformanceRecord performance1 = new PerformanceRecord();
        performance1.setSubject("Mathematics");
        performance1.setScore(85.5);
        performance1.setDate(LocalDate.of(2024, 9, 15));
        performance1.setRemarks("Good understanding of algebra");
        performance1.setStudent(student1);

        PerformanceRecord performance2 = new PerformanceRecord();
        performance2.setSubject("Physics");
        performance2.setScore(92.0);
        performance2.setDate(LocalDate.of(2024, 9, 20));
        performance2.setRemarks("Excellent lab work");
        performance2.setStudent(student1);

        PerformanceRecord performance3 = new PerformanceRecord();
        performance3.setSubject("Chemistry");
        performance3.setScore(78.5);
        performance3.setDate(LocalDate.of(2024, 9, 18));
        performance3.setRemarks("Needs improvement in organic chemistry");
        performance3.setStudent(student1);

        // Create sample performance records for student2
        PerformanceRecord performance4 = new PerformanceRecord();
        performance4.setSubject("Mathematics");
        performance4.setScore(95.0);
        performance4.setDate(LocalDate.of(2024, 9, 16));
        performance4.setRemarks("Outstanding performance");
        performance4.setStudent(student2);

        PerformanceRecord performance5 = new PerformanceRecord();
        performance5.setSubject("English Literature");
        performance5.setScore(88.0);
        performance5.setDate(LocalDate.of(2024, 9, 22));
        performance5.setRemarks("Creative writing skills are impressive");
        performance5.setStudent(student2);

        PerformanceRecord performance6 = new PerformanceRecord();
        performance6.setSubject("History");
        performance6.setScore(82.5);
        performance6.setDate(LocalDate.of(2024, 9, 19));
        performance6.setRemarks("Good analysis of historical events");
        performance6.setStudent(student2);

        // Save performance records
        performanceRepository.save(performance1);
        performanceRepository.save(performance2);
        performanceRepository.save(performance3);
        performanceRepository.save(performance4);
        performanceRepository.save(performance5);
        performanceRepository.save(performance6);

        System.out.println("Sample data initialized successfully!");
        System.out.println("Created " + studentRepository.count() + " students");
        System.out.println("Created " + performanceRepository.count() + " performance records");
        
        // BUG: Memory leak - continuously adding to static list without cleanup
        for (int i = 0; i < 1000; i++) {
            INITIALIZATION_LOG.add("Initialization step " + i + " completed at " + LocalDate.now());
        }
        System.out.println("⚠️ Memory leak: Added 1000 entries to static list (current size: " + INITIALIZATION_LOG.size() + ")");
    }
}