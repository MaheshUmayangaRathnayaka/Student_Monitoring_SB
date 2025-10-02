# JIRA BUG REPORT: SMS-004

## BASIC INFORMATION
- **Bug ID:** SMS-004
- **Title:** [Authentication] Case-sensitive email login prevents users from logging in with mixed-case emails
- **Date Reported:** October 1, 2025
- **Reporter:** QA Team
- **Assignee:** Backend Development Team
- **Watchers:** Security Team, Product Owner

## CLASSIFICATION
- **Project:** Student Monitoring System (SMS)
- **Issue Type:** Bug
- **Priority:** High
- **Severity:** Major
- **Component:** User Authentication
- **Category:** Functional Bug
- **Affects Version:** 3.1.5
- **Labels:** authentication, login, email, case-sensitivity, user-experience

## ENVIRONMENT DETAILS
- **Operating System:** Windows 11 Pro (Version 22H2)
- **Browser:** Chrome 118.0.5993.88 (64-bit)
- **Java Version:** OpenJDK 17.0.12
- **Spring Boot Version:** 3.1.5
- **Database:** H2 in-memory database
- **Test Environment:** http://localhost:8080
- **Screen Resolution:** 1920x1080
- **Browser Zoom:** 100%

## BUG DESCRIPTION

### **Summary:**
Users cannot log in with their registered email addresses if the case (uppercase/lowercase) doesn't exactly match what they entered during registration, even though email addresses should be case-insensitive by standard.

### **Detailed Description:**
The authentication system incorrectly treats email addresses as case-sensitive during login. When a user registers with an email like "John.Doe@Example.com" and later tries to log in with "john.doe@example.com" (all lowercase), the system fails to authenticate them. This violates standard email handling conventions where email addresses should be case-insensitive according to RFC 5321.

### **Business Impact:**
- **User Experience:** Users get frustrated when they can't log in with their email
- **Support Load:** Increased help desk tickets for "forgot password" requests
- **User Retention:** Users may abandon the application due to login difficulties
- **Compliance:** Violates standard email address handling practices

## REPRODUCTION STEPS

### **Prerequisites:**
1. Application must be running (http://localhost:8080)
2. Database should be initialized with default data

### **Step-by-Step Reproduction:**

1. **Start the application:**
   ```
   Navigate to: http://localhost:8080
   ```

2. **Register a new user with mixed-case email:**
   - Click "Register" or navigate to `/auth/register`
   - Fill in registration form:
     - **Email:** `TestUser@Example.Com` (note mixed case)
     - **Username:** `testuser123`
     - **Password:** `TestPassword123`
     - **Confirm Password:** `TestPassword123`
   - Click "Register" button
   - **Expected:** Registration successful, user created

3. **Log out (if automatically logged in):**
   - Click "Logout" if logged in automatically

4. **Attempt to login with lowercase email:**
   - Navigate to `/auth/login` or click "Login"
   - Enter login credentials:
     - **Email/Username:** `testuser@example.com` (note all lowercase)
     - **Password:** `TestPassword123` (correct password)
   - Click "Login" button

5. **Observe the result:**
   - Login should work but fails instead

### **Alternative Test Cases:**
- Register with `john.doe@COMPANY.COM` and login with `john.doe@company.com`
- Register with `Mary.Smith@gmail.com` and login with `MARY.SMITH@GMAIL.COM`

## EXPECTED vs ACTUAL RESULTS

### **Expected Result:**
- User should be able to log in successfully regardless of email case
- Email comparison should be case-insensitive
- Authentication should work with any combination of upper/lowercase letters in the email
- User gets redirected to dashboard/home page after successful login

### **Actual Result:**
- Login fails with incorrect credentials error
- User receives "Invalid username or password" message
- User remains on login page
- No authentication occurs despite correct password

## TECHNICAL DETAILS

### **Error Messages Observed:**
```
Login page displays: "Invalid username or password"
No specific error about case sensitivity
```

### **Browser Console Logs (F12 → Console):**
```
No JavaScript errors observed
Form submission appears normal
POST request to /auth/login returns authentication failure
```

### **Application Logs:**
```java
2025-10-01 14:30:15.123 DEBUG 1234 --- [nio-8080-exec-1] 
o.s.security.authentication.dao.DaoAuthenticationProvider: 
Authentication failed: Bad credentials 
(org.springframework.security.authentication.BadCredentialsException)

2025-10-01 14:30:15.124 DEBUG 1234 --- [nio-8080-exec-1] 
o.s.s.authentication.DefaultAuthenticationEventPublisher: 
Publishing authentication failure event [org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent]
```

### **Database Query Analysis:**
The issue appears to be in the `UserService.loadUserByUsername()` method:

**Current Problematic Code (UserService.java, line 29-31):**
```java
@Override
public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
    // BUG: Converting input to lowercase but stored emails might be mixed case
    User user = userRepository.findByUsernameOrEmail(usernameOrEmail.toLowerCase())
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));
    
    return user;
}
```

**Repository Method (UserRepository.java):**
```java
Optional<User> findByUsernameOrEmail(String username);
// This method likely searches for exact matches, including case
```

### **Root Cause Analysis:**
1. **Immediate Cause:** The `loadUserByUsername()` method converts the input email to lowercase before searching
2. **Contributing Factor:** The database stores emails in their original case from registration
3. **System Behavior:** Database query looks for exact match of lowercase input against mixed-case stored values
4. **Missing Logic:** No case-insensitive email comparison in the authentication process

## WORKAROUND
**Temporary Workaround for Users:**
- Users must enter their email address exactly as they registered it (matching the exact case)
- Alternative: Users can use their username instead of email for login (if usernames are case-insensitive)

**Technical Workaround:**
- Modify registration to store all emails in lowercase
- Add email normalization in both registration and login processes

## SUGGESTED FIX

### **Recommended Solution:**
1. **Normalize email storage:** Convert emails to lowercase during registration
2. **Normalize email lookup:** Convert input emails to lowercase during authentication
3. **Update both processes consistently**

### **Code Changes Required:**

**File: `UserService.java`**
```java
// In registerUser() method - normalize email before saving
public User registerUser(User user) throws UserRegistrationException {
    // Normalize email to lowercase
    user.setEmail(user.getEmail().toLowerCase());
    
    // Check if email already exists (case-insensitive)
    if (userRepository.existsByEmail(user.getEmail().toLowerCase())) {
        throw new UserRegistrationException("Email is already taken!");
    }
    // ... rest of method
}

// In loadUserByUsername() method - ensure consistent lookup
@Override
public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
    // For email addresses, normalize to lowercase for consistent lookup
    String normalizedInput = usernameOrEmail.toLowerCase();
    User user = userRepository.findByUsernameOrEmail(normalizedInput)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));
    
    return user;
}
```

## TEST SCENARIOS FOR VERIFICATION

### **Test Case 1: New Registration and Login**
```java
@Test
void testEmailCaseInsensitiveLogin() {
    // Register with mixed case
    User user = new User();
    user.setEmail("Test.User@Example.COM");
    user.setPassword("password123");
    userService.registerUser(user);
    
    // Login with lowercase
    UserDetails userDetails = userService.loadUserByUsername("test.user@example.com");
    assertNotNull(userDetails);
    assertEquals("test.user@example.com", userDetails.getUsername());
}
```

### **Test Case 2: Multiple Case Variations**
```java
@Test
void testMultipleEmailCaseVariations() {
    String[] emailVariations = {
        "user@example.com",
        "User@Example.com", 
        "USER@EXAMPLE.COM",
        "user@EXAMPLE.COM",
        "USER@example.com"
    };
    
    // All should authenticate the same user
    for (String email : emailVariations) {
        UserDetails userDetails = userService.loadUserByUsername(email);
        assertNotNull(userDetails, "Failed for email: " + email);
    }
}
```

## ATTACHMENTS

1. **Screenshots:**
   - `registration-mixed-case-email.png` - Registration form with mixed case email
   - `login-failure-lowercase.png` - Login failure with lowercase email
   - `error-message-invalid-credentials.png` - Error message displayed

2. **Log Files:**
   - `application-authentication-failure.log` - Application logs showing authentication failure
   - `browser-console-login-attempt.txt` - Browser console logs during failed login

3. **Test Data:**
   - `test-users-case-sensitivity.csv` - Sample user data demonstrating the issue

## ADDITIONAL INFORMATION

### **Frequency:**
- **Always reproducible** when email cases don't match between registration and login
- **Affects all users** who don't remember the exact case of their registered email
- **More common on mobile devices** where auto-capitalization changes email case

### **Related Issues:**
- Potentially similar issue with username case sensitivity (needs investigation)
- Email validation during registration may need updating
- Password reset functionality may have same case-sensitivity issue

### **Security Considerations:**
- Fix should maintain security standards
- Email normalization should not introduce new vulnerabilities
- Consider rate limiting for failed login attempts

### **Standards Compliance:**
- RFC 5321 specifies that email local parts can be case-sensitive, but most systems treat them as case-insensitive for usability
- Common web application practice is to treat emails as case-insensitive
- User experience best practices recommend case-insensitive email handling

## ACCEPTANCE CRITERIA

### **Definition of Done:**
- [ ] Users can login with any case variation of their registered email
- [ ] New registrations normalize email addresses to lowercase
- [ ] Existing users (if any) can still login after fix implementation
- [ ] All authentication-related tests pass
- [ ] No security vulnerabilities introduced
- [ ] Performance impact is minimal
- [ ] Email uniqueness constraint still enforced (case-insensitive)

### **Verification Steps:**
1. Register user with mixed-case email: `John.Doe@Example.Com`
2. Logout completely
3. Login with lowercase: `john.doe@example.com`
4. Login should succeed and user should reach dashboard
5. Verify existing users are not affected
6. Run full authentication test suite

---

**Reporter Notes:**
This bug significantly impacts user experience and should be prioritized for the next sprint. The fix is straightforward but requires careful testing to ensure existing functionality is not broken.