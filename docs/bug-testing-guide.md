# 🧪 Bug Testing Guide - Student Monitoring System

## 🎯 **Overview**
This guide provides step-by-step instructions to test all 6 intentionally introduced bugs in the Student Monitoring System. Each bug represents a different category of real-world software defects.

---

## 🔍 **BUG TESTING INSTRUCTIONS**

### **🐛 Bug #1: Case-Sensitive Email Login**
**Type:** Authentication | **Severity:** Major | **File:** `UserService.java`

#### **How to Test:**
```bash
# 1. Start the application
cd C:\Users\usr\Desktop\Smon\Student_Monitoring_SB
.\mvnw spring-boot:run

# 2. Navigate to: http://localhost:8080/auth/register
```

#### **Test Steps:**
1. **Register new user:**
   - Email: `TestUser@Example.Com` (mixed case)
   - Username: `testuser123`
   - Password: `TestPass123`
   - Confirm Password: `TestPass123`
   - Click "Register"

2. **Logout** (if auto-logged in)

3. **Try to login:**
   - Email: `testuser@example.com` (all lowercase)
   - Password: `TestPass123`
   - Click "Login"

#### **Expected Bug Behavior:**
- ❌ Login fails with "Invalid username or password"
- ✅ **Bug Confirmed:** Case-sensitive email authentication

#### **Bug Report ID:** SMS-004

---

### **🐛 Bug #2: SQL Injection Vulnerability**
**Type:** Security | **Severity:** Critical | **File:** `StudentController.java`

#### **How to Test:**
```bash
# 1. Start application (if not running)
# 2. Test the vulnerable search endpoint
```

#### **Test Methods:**

**Method 1: Browser URL**
```
http://localhost:8080/api/students/search?query=test'; DROP TABLE students; --
```

**Method 2: Postman/curl**
```bash
curl "http://localhost:8080/api/students/search?query=test%27%3B%20DROP%20TABLE%20students%3B%20--"
```

**Method 3: Browser Console**
```javascript
fetch('/api/students/search?query=test\'; DROP TABLE students; --')
  .then(response => response.json())
  .then(data => console.log(data));
```

#### **Expected Bug Behavior:**
- ⚠️ Console shows: "SECURITY ALERT: Potential SQL injection detected"
- ⚠️ Logs dangerous SQL query construction
- ✅ **Bug Confirmed:** SQL injection vulnerability exists

#### **Bug Report ID:** SMS-005

---

### **🐛 Bug #3: Null Pointer Exception**
**Type:** Runtime | **Severity:** Major | **File:** `PerformanceServiceImpl.java`

#### **How to Test:**
This bug requires calling a method programmatically. Create a test or use the application:

#### **Test Steps:**
1. **Create a student with no performance records**
2. **Call the vulnerable method:**

```java
// This would be in a test or controller
PerformanceServiceImpl performanceService;
Double average = performanceService.calculateAverageScore(studentId); // NPE here!
```

#### **Alternative Test via REST endpoint:**
You would need to add this endpoint to test:
```java
@GetMapping("/students/{id}/average")
public ResponseEntity<Double> getAverageScore(@PathVariable Long id) {
    Double average = performanceService.calculateAverageScore(id);
    return ResponseEntity.ok(average);
}
```

#### **Expected Bug Behavior:**
- ❌ `NullPointerException` when calling method for student with no records
- ✅ **Bug Confirmed:** NPE in average calculation

#### **Bug Report ID:** SMS-006

---

### **🐛 Bug #4: Memory Leak**
**Type:** Performance | **Severity:** Medium | **File:** `DataInitializer.java`

#### **How to Test:**
```bash
# 1. Start application and watch logs
.\mvnw spring-boot:run

# 2. Look for this message in console:
# "⚠️ Memory leak: Added 1000 entries to static list (current size: X)"

# 3. Restart application multiple times
# Ctrl+C to stop, then start again
.\mvnw spring-boot:run

# 4. Each restart adds 1000 more entries to static list
```

#### **Monitor Memory:**
```bash
# Check memory usage (Windows)
tasklist /fi "imagename eq java.exe"

# Or use JVisualVM, JProfiler to monitor heap
```

#### **Expected Bug Behavior:**
- 🔄 Each application restart adds 1000 entries to static list
- 📈 Memory usage grows with each restart
- ✅ **Bug Confirmed:** Memory leak in initialization

#### **Bug Report ID:** SMS-007

---

### **🐛 Bug #5: UI Validation Bug**
**Type:** Frontend | **Severity:** Minor | **File:** `register.html`

#### **How to Test:**
```bash
# 1. Navigate to registration page
http://localhost:8080/auth/register
```

#### **Test Steps:**
1. **Fill registration form:**
   - Email: `testuser@example.com`
   - Username: `testuser`
   - Password: `password123`
   - Confirm Password: `password123` (exactly matching)

2. **Click "Register"**

3. **Open browser console (F12) and check for errors**

#### **Expected Bug Behavior:**
- ❌ Form shows validation error even when passwords match
- 🐛 Console shows: "🐛 BUG: Password validation is inverted!"
- ✅ **Bug Confirmed:** Inverted validation logic

#### **Bug Report ID:** SMS-008

---

### **🐛 Bug #6: Race Condition**
**Type:** Concurrency | **Severity:** Major | **File:** `StudentServiceImpl.java`

#### **How to Test:**
This requires concurrent requests. Use multiple browser tabs or tools:

#### **Method 1: Multiple Browser Tabs**
```bash
# 1. Open 2-3 browser tabs
# 2. Navigate all to: http://localhost:8080/students/add (if exists)
# 3. Try to create students with same email simultaneously
```

#### **Method 2: Automated Testing**
```bash
# Create concurrent curl requests
curl -X POST http://localhost:8080/api/students -H "Content-Type: application/json" -d '{"email":"race@test.com","firstName":"Test","lastName":"User"}' &
curl -X POST http://localhost:8080/api/students -H "Content-Type: application/json" -d '{"email":"race@test.com","firstName":"Test","lastName":"User"}' &
```

#### **Expected Bug Behavior:**
- ⚡ One request succeeds, another might fail with constraint violation
- 🐛 Race condition window created by Thread.sleep(100)
- ✅ **Bug Confirmed:** Concurrent creation can cause issues

#### **Bug Report ID:** SMS-009

---

## 📊 **BUG DISCOVERY CHECKLIST**

| Bug # | Type | Severity | Status | Bug ID |
|-------|------|----------|--------|--------|
| 1 | Authentication | Major | ⬜ Tested | SMS-004 |
| 2 | Security | Critical | ⬜ Tested | SMS-005 |
| 3 | Runtime | Major | ⬜ Tested | SMS-006 |
| 4 | Performance | Medium | ⬜ Tested | SMS-007 |
| 5 | Frontend | Minor | ⬜ Tested | SMS-008 |
| 6 | Concurrency | Major | ⬜ Tested | SMS-009 |

---

## 📝 **NEXT STEPS AFTER TESTING**

### **For Each Bug Found:**
1. ✅ **Test and reproduce** the bug following steps above
2. 📋 **Create comprehensive bug report** using template from tutorial
3. 🔍 **Perform root cause analysis**
4. 🔧 **Fix the bug** (optional - for learning)
5. ✅ **Verify fix works** with regression testing

### **Bug Report Templates:**
- Use the comprehensive Jira template from `05-Defect-Tracking-Tutorial.md`
- Include screenshots, logs, and reproduction steps
- Assign appropriate priority and severity
- Document business impact

### **Learning Outcomes:**
- 🎯 **Bug Discovery Skills** - Find different types of defects
- 📝 **Professional Reporting** - Create industry-standard bug reports  
- 🔧 **Root Cause Analysis** - Understand why bugs occur
- ✅ **Quality Assurance** - Verify fixes and prevent regression

---

## 🚀 **SUCCESS CRITERIA**

You'll have successfully completed the defect tracking tutorial when you can:

- ✅ Discover and reproduce all 6 bugs
- ✅ Create comprehensive bug reports for each
- ✅ Understand different bug categories and severities
- ✅ Perform root cause analysis
- ✅ Track bugs through their lifecycle (New → Fixed → Verified)

**Happy Bug Hunting! 🐛🔍**