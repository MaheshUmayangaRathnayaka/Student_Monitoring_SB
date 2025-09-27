# Software Quality Metrics and Standards Tutorial

## Overview
This tutorial covers Defect Density, Mean Time to Failure (MTTF), and SonarQube analysis for measuring software quality. Learn to measure and improve code quality systematically.

## 🎯 Step-by-Step Quality Metrics Instructions

### Quick Quality Analysis Commands

```bash
# Navigate to project directory
cd c:\Users\usr\Desktop\Smon\Student_Monitoring_SB

# Generate test coverage report
.\mvnw test jacoco:report

# Run SonarQube analysis (requires SonarQube server)
.\mvnw sonar:sonar

# Generate project metrics
.\mvnw site

# View reports:
# - Coverage: target/site/jacoco/index.html
# - Site: target/site/index.html
```

## Part 1: Defect Density Calculation

### Step 1: Code Measurement

#### Module Selection: Authentication Service
The Authentication Service module contains core login/registration functionality and is critical for security.

**Files in Module:**
```
src/main/java/com/example/studentmonitor/service/
├── AuthService.java (interface)
├── impl/AuthServiceImpl.java 
src/main/java/com/example/studentmonitor/config/
├── SecurityConfig.java
src/main/java/com/example/studentmonitor/controller/
├── AuthController.java
src/main/java/com/example/studentmonitor/dto/
├── SignupRequest.java
├── LoginRequest.java
```

#### Step 2: Count Lines of Code (LOC)

**Using IDE/Manual Count:**

**File:** `src/main/java/com/example/studentmonitor/service/AuthService.java`
```java
package com.example.studentmonitor.service;

import com.example.studentmonitor.dto.SignupRequest;
import com.example.studentmonitor.model.User;

public interface AuthService {
    User signup(SignupRequest signupRequest);
    User login(String email, String password);
}
```
**LOC: 8 lines**

## 🟢 QUALITY METRICS TEST 1: Code Coverage Analysis

### Step 1: Execute Code Coverage Test

**Execute these commands to generate coverage metrics:**
```bash
# Run all tests with coverage
.\mvnw clean test jacoco:report

# Open coverage report in browser
# File: target/site/jacoco/index.html
```

**Expected Output:** ✅ Coverage report showing percentage of code tested

### Step 2: Analyze Coverage Results

**Coverage Report Analysis:**
```
Overall Coverage Target: > 80%
├── Line Coverage: Should be > 85%
├── Branch Coverage: Should be > 75%
├── Method Coverage: Should be > 90%
└── Class Coverage: Should be > 95%

Example Coverage Results:
- AuthServiceImpl: 92% line coverage ✅
- StudentServiceImpl: 88% line coverage ✅
- AuthController: 76% line coverage ⚠️ (needs improvement)
- StudentController: 85% line coverage ✅
```

### Step 3: Execute Coverage Analysis - Step by Step
```bash
# 1. Clean and run tests with coverage
.\mvnw clean test jacoco:report

# Expected output:
# - Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
# - [INFO] Loading execution data file target/jacoco.exec
# - [INFO] Analyzed bundle 'student-monitor'
# - [INFO] BUILD SUCCESS

# 2. Open coverage report
start target/site/jacoco/index.html

# Expected browser content:
# - Overall coverage percentage
# - Package-wise coverage breakdown  
# - Class-wise coverage details
# - Method-wise coverage analysis
```

**File:** `src/main/java/com/example/studentmonitor/service/impl/AuthServiceImpl.java`
import com.example.studentmonitor.repository.UserRepository;
import com.example.studentmonitor.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public User signup(SignupRequest signupRequest) {
        validateSignupRequest(signupRequest);
        
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email already taken");
        }
        
        User user = createUserFromRequest(signupRequest);
        return userRepository.save(user);
    }
    
    @Override
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
            
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        return user;
    }
    
    private void validateSignupRequest(SignupRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }
    
    private User createUserFromRequest(SignupRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUsername(request.getEmail());
        user.setFirstName("DefaultFirst");
        user.setLastName("DefaultLast");
        return user;
    }
}
```
**LOC: 52 lines**

**Total LOC Count:**
- AuthService.java: 8 lines
- AuthServiceImpl.java: 52 lines  
- SecurityConfig.java: 45 lines
- AuthController.java: 38 lines
- SignupRequest.java: 15 lines
- LoginRequest.java: 12 lines

**Total Module LOC: 170 lines**

#### Step 3: Count Defects

**Defects Found in Authentication Module (from testing):**
1. **SMS-001 (Critical):** Authentication bypass vulnerability
2. **SMS-004 (Major):** Password validation accepts weak passwords
3. **SMS-007 (Minor):** Error messages expose user enumeration

**Total Defects: 3**

#### Step 4: Calculate Defect Density

**Formula:** Defect Density = (Total Defects / Total LOC) × 1000

**Calculation:**
```
Defect Density = (3 defects / 170 LOC) × 1000
               = 0.0176 × 1000
               = 17.6 defects per 1000 LOC
```

**Industry Benchmark:** 
- Good: < 10 defects per 1000 LOC
- Average: 10-20 defects per 1000 LOC  
- Poor: > 20 defects per 1000 LOC

**Analysis:** Our module has 17.6 defects per 1000 LOC, which is in the average range but approaching the upper limit.

### Automated LOC Counting

**Using Maven Plugin:**

**File:** `pom.xml` (Add LOC counting plugin)
```xml
<plugin>
    <groupId>com.github.dantwining.whitespace-maven-plugin</groupId>
    <artifactId>whitespace-maven-plugin</artifactId>
    <version>1.0.4</version>
    <executions>
        <execution>
            <goals>
                <goal>trim</goal>
            </goals>
        </execution>
    </executions>
</plugin>

<!-- LOC Counter Plugin -->
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>cloc-maven-plugin</artifactId>
    <version>1.0.0</version>
</plugin>
```

**Commands:**
```bash
# Count lines of code
find src/main/java -name "*.java" | xargs wc -l

# Using cloc tool
cloc src/main/java/com/example/studentmonitor/service/
cloc src/main/java/com/example/studentmonitor/config/SecurityConfig.java

# Generate detailed report
./mvnw clean compile exec:java -Dexec.mainClass="org.example.LOCCounter"
```

## Part 2: Mean Time to Failure (MTTF)

### Step 1: Understanding MTTF

**Definition:** MTTF is the average time between failures for a system or component.

**Formula:** MTTF = Total Operating Time / Number of Failures

### Step 2: Data Collection

**Test Execution Data (Example):**

**File:** `docs/test-execution-log.md`
```markdown
# Test Execution Log - Authentication Module

## Test Cycle 1 (2024-01-01 to 2024-01-07)
- **Total Test Time:** 40 hours
- **Test Cases Executed:** 150
- **Failures:** 2
- **Operating Time:** 168 hours (1 week)

## Test Cycle 2 (2024-01-08 to 2024-01-14)  
- **Total Test Time:** 45 hours
- **Test Cases Executed:** 180
- **Failures:** 1
- **Operating Time:** 168 hours (1 week)

## Test Cycle 3 (2024-01-15 to 2024-01-21)
- **Total Test Time:** 50 hours
- **Test Cases Executed:** 200
- **Failures:** 3
- **Operating Time:** 168 hours (1 week)

## Summary
- **Total Operating Time:** 504 hours (3 weeks)
- **Total Failures:** 6
- **MTTF:** 504 / 6 = 84 hours
```

### Step 3: MTTF Calculation Script

**File:** `scripts/calculate-mttf.py`
```python
#!/usr/bin/env python3
"""
MTTF Calculator for Software Testing
"""

class MTTFCalculator:
    def __init__(self):
        self.test_cycles = []
    
    def add_test_cycle(self, cycle_name, operating_hours, failures, test_cases):
        """Add a test cycle data point"""
        self.test_cycles.append({
            'name': cycle_name,
            'operating_hours': operating_hours,
            'failures': failures,
            'test_cases': test_cases
        })
    
    def calculate_mttf(self):
        """Calculate overall MTTF"""
        total_operating_time = sum(cycle['operating_hours'] for cycle in self.test_cycles)
        total_failures = sum(cycle['failures'] for cycle in self.test_cycles)
        
        if total_failures == 0:
            return float('inf')  # No failures
        
        mttf = total_operating_time / total_failures
        return mttf
    
    def calculate_failure_rate(self):
        """Calculate failure rate (failures per hour)"""
        mttf = self.calculate_mttf()
        if mttf == float('inf'):
            return 0
        return 1 / mttf
    
    def generate_report(self):
        """Generate MTTF analysis report"""
        total_operating_time = sum(cycle['operating_hours'] for cycle in self.test_cycles)
        total_failures = sum(cycle['failures'] for cycle in self.test_cycles)
        total_test_cases = sum(cycle['test_cases'] for cycle in self.test_cycles)
        mttf = self.calculate_mttf()
        failure_rate = self.calculate_failure_rate()
        
        print("=== MTTF Analysis Report ===")
        print(f"Total Test Cycles: {len(self.test_cycles)}")
        print(f"Total Operating Time: {total_operating_time} hours")
        print(f"Total Failures: {total_failures}")
        print(f"Total Test Cases: {total_test_cases}")
        print(f"MTTF: {mttf:.2f} hours")
        print(f"Failure Rate: {failure_rate:.6f} failures/hour")
        print(f"Reliability (24h): {(1 - (24 * failure_rate)) * 100:.2f}%")
        
        # Cycle-by-cycle analysis
        print("\n=== Cycle Analysis ===")
        for i, cycle in enumerate(self.test_cycles, 1):
            cycle_failure_rate = cycle['failures'] / cycle['operating_hours'] if cycle['operating_hours'] > 0 else 0
            print(f"Cycle {i} ({cycle['name']}):")
            print(f"  Operating Time: {cycle['operating_hours']} hours")
            print(f"  Failures: {cycle['failures']}")
            print(f"  Failure Rate: {cycle_failure_rate:.6f} failures/hour")

if __name__ == "__main__":
    # Initialize calculator
    calculator = MTTFCalculator()
    
    # Add test cycle data
    calculator.add_test_cycle("Week 1", 168, 2, 150)
    calculator.add_test_cycle("Week 2", 168, 1, 180)
    calculator.add_test_cycle("Week 3", 168, 3, 200)
    calculator.add_test_cycle("Week 4", 168, 1, 220)
    
    # Generate report
    calculator.generate_report()
```

**Run the script:**
```bash
python3 scripts/calculate-mttf.py
```

**Expected Output:**
```
=== MTTF Analysis Report ===
Total Test Cycles: 4
Total Operating Time: 672 hours
Total Failures: 7
Total Test Cases: 750
MTTF: 96.00 hours
Failure Rate: 0.010417 failures/hour
Reliability (24h): 75.00%

=== Cycle Analysis ===
Cycle 1 (Week 1):
  Operating Time: 168 hours
  Failures: 2
  Failure Rate: 0.011905 failures/hour
...
```

### Step 4: MTTF Improvement Tracking

**File:** `docs/mttf-improvement-plan.md`
```markdown
# MTTF Improvement Plan

## Current State
- **MTTF:** 96 hours
- **Target MTTF:** 200 hours (reduce failure rate by 50%)

## Improvement Actions

### Short-term (1-2 weeks)
1. **Fix Critical Bugs**
   - SMS-001: Authentication bypass (DONE)
   - SMS-004: Password validation (IN PROGRESS)
   
2. **Enhance Test Coverage**
   - Add security test cases: +20 test cases
   - Add edge case testing: +15 test cases

### Medium-term (1 month)
1. **Code Quality Improvements**
   - Refactor complex methods (cyclomatic complexity > 10)
   - Add input validation
   - Improve error handling

2. **Monitoring & Alerting**
   - Add application monitoring
   - Set up failure alerts
   - Track failure patterns

### Expected Results
- **Week 5 MTTF Target:** 120 hours
- **Week 8 MTTF Target:** 150 hours  
- **Week 12 MTTF Target:** 200 hours
```

## Part 3: SonarQube Analysis

### Step 1: SonarQube Setup

#### Option A: SonarCloud (Recommended for learning)
1. **Create Account:**
   - Go to https://sonarcloud.io
   - Sign up with GitHub account
   - Import your repository

2. **Generate Token:**
   - User Settings → Security → Generate Token
   - Copy token for CI/CD use

#### Option B: Local SonarQube
```bash
# Download SonarQube Community Edition
wget https://binaries.sonarsource.com/Distribution/sonarqube/sonarqube-9.9.2.77730.zip
unzip sonarqube-9.9.2.77730.zip
cd sonarqube-9.9.2.77730/bin/linux-x86-64/
./sonar.sh start

# Access at http://localhost:9000
# Default login: admin/admin
```

### Step 2: Configure SonarQube Analysis

**File:** `pom.xml` (Add SonarQube plugin)
```xml
<properties>
    <!-- SonarQube Configuration -->
    <sonar.organization>your-org</sonar.organization>
    <sonar.host.url>https://sonarcloud.io</sonar.host.url>
    <sonar.projectKey>student-monitoring-system</sonar.projectKey>
    <sonar.coverage.jacoco.xmlReportPaths>target/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
</properties>

<plugins>
    <!-- SonarQube Scanner -->
    <plugin>
        <groupId>org.sonarsource.scanner.maven</groupId>
        <artifactId>sonar-maven-plugin</artifactId>
        <version>3.9.1.2184</version>
    </plugin>
    
    <!-- JaCoCo for Test Coverage -->
    <plugin>
        <groupId>org.jacoco</groupId>
        <artifactId>jacoco-maven-plugin</artifactId>
        <version>0.8.8</version>
        <executions>
            <execution>
                <goals>
                    <goal>prepare-agent</goal>
                </goals>
            </execution>
            <execution>
                <id>report</id>
                <phase>test</phase>
                <goals>
                    <goal>report</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
</plugins>
```

### Step 3: Run SonarQube Analysis

**Commands:**
```bash
# Run tests with coverage
./mvnw clean test jacoco:report

# Run SonarQube analysis (local)
./mvnw sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=admin -Dsonar.password=admin

# Run SonarQube analysis (SonarCloud)
./mvnw sonar:sonar -Dsonar.login=your-token

# Run with specific profile
./mvnw clean test jacoco:report sonar:sonar -Psonar
```

### Step 4: SonarQube Results Analysis

#### Code Smells Found

**Example Issues:**

1. **Duplicated Code:**
```java
// BEFORE: Duplicate validation logic
public class AuthServiceImpl {
    public User signup(SignupRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        // ... rest of method
    }
    
    public User login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        // ... rest of method
    }
}

// AFTER: Extracted common validation
public class ValidationUtils {
    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
    }
}
```

2. **Complex Method (High Cyclomatic Complexity):**
```java
// BEFORE: Complex method (Complexity: 12)
public User processRegistration(SignupRequest request) {
    if (request.getEmail() == null || request.getEmail().isEmpty()) {
        throw new IllegalArgumentException("Email required");
    }
    if (request.getPassword() == null || request.getPassword().length() < 6) {
        throw new IllegalArgumentException("Password too short");
    }
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new IllegalArgumentException("Email exists");
    }
    if (request.getFirstName() == null || request.getFirstName().isEmpty()) {
        request.setFirstName("Unknown");
    }
    if (request.getLastName() == null || request.getLastName().isEmpty()) {
        request.setLastName("User");
    }
    // ... more conditions
    return userRepository.save(user);
}

// AFTER: Refactored (Complexity: 3)
public User processRegistration(SignupRequest request) {
    validateRegistrationRequest(request);
    setDefaultValues(request);
    User user = createUserFromRequest(request);
    return userRepository.save(user);
}

private void validateRegistrationRequest(SignupRequest request) {
    ValidationUtils.validateEmail(request.getEmail());
    ValidationUtils.validatePassword(request.getPassword());
    checkEmailAvailability(request.getEmail());
}
```

3. **Security Vulnerabilities:**
```java
// BEFORE: Hardcoded secret
@Value("${jwt.secret:mySecretKey123}")
private String jwtSecret;

// AFTER: Environment-based secret
@Value("${jwt.secret}")
private String jwtSecret;
```

### Step 5: SonarQube Quality Gate

**File:** `sonar-project.properties`
```properties
# Project Information
sonar.projectKey=student-monitoring-system
sonar.projectName=Student Monitoring System
sonar.projectVersion=1.0

# Source Configuration
sonar.sources=src/main/java
sonar.tests=src/test/java
sonar.java.binaries=target/classes
sonar.java.test.binaries=target/test-classes

# Coverage Configuration
sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml

# Quality Gate Conditions
sonar.qualitygate.wait=true

# Exclusions
sonar.exclusions=**/*DTO.java,**/*Entity.java,**/*Config.java
sonar.test.exclusions=**/*Test.java

# Duplicate Detection
sonar.cpd.java.minimumtokens=50
```

### Step 6: Remediation Actions

**File:** `docs/sonarqube-remediation-plan.md`
```markdown
# SonarQube Remediation Plan

## Analysis Results Summary
- **Overall Rating:** C
- **Code Smells:** 23
- **Bugs:** 2  
- **Vulnerabilities:** 1
- **Coverage:** 67%
- **Duplicated Lines:** 3.2%

## Priority Issues

### Critical (Must Fix)
1. **Security Vulnerability:** Hardcoded JWT secret
   - **File:** SecurityConfig.java:42
   - **Action:** Move to environment variables
   - **ETA:** 1 day

2. **Bug:** Null pointer exception possibility
   - **File:** AuthServiceImpl.java:78
   - **Action:** Add null checks
   - **ETA:** 2 hours

### High Priority
1. **Code Smell:** Complex method (Complexity: 15)
   - **File:** StudentServiceImpl.java:156
   - **Action:** Refactor into smaller methods
   - **ETA:** 4 hours

2. **Duplicated Code:** Validation logic (12 occurrences)
   - **Files:** Multiple service classes
   - **Action:** Extract to utility class
   - **ETA:** 3 hours

### Medium Priority
1. **Coverage:** Low test coverage (67%)
   - **Target:** Increase to 80%
   - **Action:** Add unit tests for service layer
   - **ETA:** 2 days

## Remediation Progress Tracking

### Week 1
- [x] Fix security vulnerability (JWT secret)
- [x] Fix null pointer exception
- [ ] Refactor complex methods
- [ ] Extract validation utilities

### Week 2  
- [ ] Increase test coverage to 75%
- [ ] Fix remaining code smells
- [ ] Set up quality gate

### Quality Gate Targets
- **Maintainability Rating:** A
- **Reliability Rating:** A  
- **Security Rating:** A
- **Coverage:** > 80%
- **Duplicated Lines:** < 3%
```

## Part 4: Quality Metrics Dashboard

### Step 1: Create Metrics Tracking

**File:** `scripts/quality-metrics.py`
```python
#!/usr/bin/env python3
"""
Software Quality Metrics Dashboard
"""

import json
import requests
from datetime import datetime, timedelta

class QualityMetrics:
    def __init__(self):
        self.metrics = {
            'defect_density': {},
            'mttf': {},
            'test_coverage': {},
            'code_quality': {}
        }
    
    def calculate_defect_density(self, module_name, loc, defects):
        """Calculate defect density for a module"""
        density = (defects / loc) * 1000 if loc > 0 else 0
        self.metrics['defect_density'][module_name] = {
            'loc': loc,
            'defects': defects,
            'density': density,
            'date': datetime.now().isoformat()
        }
        return density
    
    def calculate_mttf(self, operating_hours, failures):
        """Calculate MTTF"""
        mttf = operating_hours / failures if failures > 0 else float('inf')
        self.metrics['mttf'] = {
            'operating_hours': operating_hours,
            'failures': failures,
            'mttf': mttf,
            'date': datetime.now().isoformat()
        }
        return mttf
    
    def fetch_sonarqube_metrics(self, project_key, sonar_url, token):
        """Fetch metrics from SonarQube API"""
        try:
            url = f"{sonar_url}/api/measures/component"
            params = {
                'component': project_key,
                'metricKeys': 'coverage,bugs,vulnerabilities,code_smells,duplicated_lines_density'
            }
            headers = {'Authorization': f'Bearer {token}'}
            
            response = requests.get(url, params=params, headers=headers)
            data = response.json()
            
            measures = {}
            for measure in data['component']['measures']:
                measures[measure['metric']] = float(measure['value'])
            
            self.metrics['code_quality'] = {
                'coverage': measures.get('coverage', 0),
                'bugs': measures.get('bugs', 0),
                'vulnerabilities': measures.get('vulnerabilities', 0),
                'code_smells': measures.get('code_smells', 0),
                'duplicated_lines': measures.get('duplicated_lines_density', 0),
                'date': datetime.now().isoformat()
            }
            
            return measures
        except Exception as e:
            print(f"Error fetching SonarQube metrics: {e}")
            return {}
    
    def generate_dashboard(self):
        """Generate quality metrics dashboard"""
        print("=" * 60)
        print("SOFTWARE QUALITY METRICS DASHBOARD")
        print("=" * 60)
        
        # Defect Density
        print("\n📊 DEFECT DENSITY")
        print("-" * 40)
        for module, data in self.metrics['defect_density'].items():
            print(f"{module}:")
            print(f"  LOC: {data['loc']}")
            print(f"  Defects: {data['defects']}")
            print(f"  Density: {data['density']:.2f} defects/1000 LOC")
            
            # Quality assessment
            if data['density'] < 10:
                status = "✅ GOOD"
            elif data['density'] < 20:
                status = "⚠️ AVERAGE"
            else:
                status = "❌ POOR"
            print(f"  Status: {status}")
        
        # MTTF
        print("\n⏱️ MEAN TIME TO FAILURE")
        print("-" * 40)
        mttf_data = self.metrics['mttf']
        if mttf_data:
            print(f"Operating Hours: {mttf_data['operating_hours']}")
            print(f"Failures: {mttf_data['failures']}")
            print(f"MTTF: {mttf_data['mttf']:.2f} hours")
            
            reliability_24h = (1 - (24 / mttf_data['mttf'])) * 100 if mttf_data['mttf'] != float('inf') else 100
            print(f"24h Reliability: {reliability_24h:.2f}%")
        
        # Code Quality
        print("\n🔍 CODE QUALITY (SonarQube)")
        print("-" * 40)
        quality = self.metrics['code_quality']
        if quality:
            print(f"Test Coverage: {quality['coverage']:.1f}%")
            print(f"Bugs: {quality['bugs']}")
            print(f"Vulnerabilities: {quality['vulnerabilities']}")
            print(f"Code Smells: {quality['code_smells']}")
            print(f"Duplicated Lines: {quality['duplicated_lines']:.1f}%")
    
    def save_metrics(self, filename='quality_metrics.json'):
        """Save metrics to JSON file"""
        with open(filename, 'w') as f:
            json.dump(self.metrics, f, indent=2)
        print(f"\nMetrics saved to {filename}")

if __name__ == "__main__":
    # Initialize metrics calculator
    metrics = QualityMetrics()
    
    # Calculate defect density for modules
    metrics.calculate_defect_density("Authentication", 170, 3)
    metrics.calculate_defect_density("Student Management", 250, 2)
    metrics.calculate_defect_density("Performance Tracking", 180, 4)
    
    # Calculate MTTF
    metrics.calculate_mttf(672, 7)  # 4 weeks, 7 failures
    
    # Mock SonarQube data (replace with real API call)
    metrics.metrics['code_quality'] = {
        'coverage': 73.2,
        'bugs': 2,
        'vulnerabilities': 1,
        'code_smells': 15,
        'duplicated_lines': 3.8,
        'date': datetime.now().isoformat()
    }
    
    # Generate dashboard
    metrics.generate_dashboard()
    
    # Save metrics
    metrics.save_metrics()
```

### Step 2: Quality Trends Tracking

**File:** `scripts/quality-trends.py`
```python
#!/usr/bin/env python3
"""
Track quality metrics trends over time
"""

import matplotlib.pyplot as plt
import pandas as pd
from datetime import datetime, timedelta

def generate_quality_trends():
    # Sample data for 8 weeks
    weeks = list(range(1, 9))
    
    # Defect density trend (should decrease)
    defect_density = [22.5, 20.1, 18.7, 17.6, 15.2, 13.8, 12.1, 10.5]
    
    # MTTF trend (should increase)
    mttf = [72, 84, 96, 108, 125, 142, 158, 175]
    
    # Test coverage trend (should increase)
    coverage = [58, 62, 67, 73, 78, 82, 85, 87]
    
    # Code smells trend (should decrease)
    code_smells = [45, 38, 32, 23, 18, 15, 12, 8]
    
    # Create subplots
    fig, ((ax1, ax2), (ax3, ax4)) = plt.subplots(2, 2, figsize=(15, 10))
    
    # Defect Density
    ax1.plot(weeks, defect_density, 'r-o', linewidth=2, markersize=6)
    ax1.set_title('Defect Density Trend', fontsize=14, fontweight='bold')
    ax1.set_xlabel('Week')
    ax1.set_ylabel('Defects per 1000 LOC')
    ax1.grid(True, alpha=0.3)
    ax1.axhline(y=10, color='g', linestyle='--', alpha=0.7, label='Target')
    ax1.legend()
    
    # MTTF
    ax2.plot(weeks, mttf, 'b-o', linewidth=2, markersize=6)
    ax2.set_title('Mean Time to Failure Trend', fontsize=14, fontweight='bold')
    ax2.set_xlabel('Week')
    ax2.set_ylabel('MTTF (hours)')
    ax2.grid(True, alpha=0.3)
    ax2.axhline(y=200, color='g', linestyle='--', alpha=0.7, label='Target')
    ax2.legend()
    
    # Test Coverage
    ax3.plot(weeks, coverage, 'g-o', linewidth=2, markersize=6)
    ax3.set_title('Test Coverage Trend', fontsize=14, fontweight='bold')
    ax3.set_xlabel('Week')
    ax3.set_ylabel('Coverage (%)')
    ax3.grid(True, alpha=0.3)
    ax3.axhline(y=80, color='g', linestyle='--', alpha=0.7, label='Target')
    ax3.legend()
    
    # Code Smells
    ax4.plot(weeks, code_smells, 'm-o', linewidth=2, markersize=6)
    ax4.set_title('Code Smells Trend', fontsize=14, fontweight='bold')
    ax4.set_xlabel('Week')
    ax4.set_ylabel('Number of Code Smells')
    ax4.grid(True, alpha=0.3)
    ax4.axhline(y=10, color='g', linestyle='--', alpha=0.7, label='Target')
    ax4.legend()
    
    plt.tight_layout()
    plt.savefig('quality_trends.png', dpi=300, bbox_inches='tight')
    plt.show()

if __name__ == "__main__":
    generate_quality_trends()
```

## Commands Summary

### Defect Density
```bash
# Count lines of code
find src/main/java -name "*.java" | xargs wc -l

# Count defects from bug tracking
# Manual count from Jira/Bugzilla

# Calculate using script
python3 scripts/quality-metrics.py
```

### MTTF Calculation  
```bash
# Run MTTF calculation
python3 scripts/calculate-mttf.py

# Track trends
python3 scripts/quality-trends.py
```

### SonarQube Analysis
```bash
# Run analysis
./mvnw clean test jacoco:report sonar:sonar

# View results
# Open http://localhost:9000 or SonarCloud dashboard

# Fix issues and re-run
./mvnw sonar:sonar
```

## Screenshots to Capture

1. **Defect Density Calculation:** Spreadsheet or script output
2. **MTTF Analysis:** Graph showing MTTF trends
3. **SonarQube Dashboard:** Overall project health
4. **Code Smells:** Before and after remediation
5. **Quality Trends:** Multi-week improvement charts
6. **Quality Gate:** Pass/fail status in SonarQube

## Key Deliverables

1. ✅ **Defect Density:** 17.6 defects per 1000 LOC for Authentication module
2. ✅ **MTTF Analysis:** 96 hours with improvement plan to 200 hours
3. ✅ **SonarQube Report:** Code quality assessment with remediation plan
4. ✅ **Quality Metrics Dashboard:** Automated tracking and reporting
5. ✅ **Improvement Trends:** 8-week quality improvement visualization