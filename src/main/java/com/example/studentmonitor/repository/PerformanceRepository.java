package com.example.studentmonitor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.studentmonitor.model.PerformanceRecord;

@Repository
public interface PerformanceRepository extends JpaRepository<PerformanceRecord, Long> {
    
    List<PerformanceRecord> findByStudentId(Long studentId);
}