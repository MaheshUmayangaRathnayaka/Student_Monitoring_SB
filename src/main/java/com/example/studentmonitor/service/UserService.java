package com.example.studentmonitor.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.studentmonitor.model.User;
import com.example.studentmonitor.repository.UserRepository;

@Service
@Transactional
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // BUG INTRODUCED: Case-sensitive email login - convert to lowercase for lookup
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));
        
        return user;
    }
    
    /**
     * Register a new user
     */
    public User registerUser(User user) throws UserRegistrationException {
        // Check if email already exists first
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserRegistrationException("Email is already taken!");
        }
        
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserRegistrationException("Username is already taken!");
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default role if not set
        if (user.getRole() == null) {
            user.setRole(User.Role.USER);
        }
        
        // Save user
        return userRepository.save(user);
    }
    
    /**
     * Find user by username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Find user by ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get users by role
     */
    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Update user
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    /**
     * Delete user by ID
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    /**
     * Check if user exists by username
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Check if user exists by email
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Change user password
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) throws PasswordChangeException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PasswordChangeException("User not found"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new PasswordChangeException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    /**
     * Enable or disable user
     */
    public void setUserEnabled(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(enabled);
        userRepository.save(user);
    }
    
    /**
     * Get total user count
     */
    public long getTotalUserCount() {
        return userRepository.count();
    }
    
    /**
     * Get active user count
     */
    public long getActiveUserCount() {
        return userRepository.countByEnabledTrue();
    }
    
    /**
     * Get admin count
     */
    public long getAdminCount() {
        return userRepository.countByRole(User.Role.ADMIN);
    }
    
    /**
     * Create admin user if not exists
     */
    public void createAdminIfNotExists() {
        if (userRepository.countByRole(User.Role.ADMIN) == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@studentmonitor.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
        }
    }
    
    /**
     * Custom exception for user registration errors
     */
    public static class UserRegistrationException extends Exception {
        public UserRegistrationException(String message) {
            super(message);
        }
    }
    
    /**
     * Custom exception for password change errors
     */
    public static class PasswordChangeException extends Exception {
        public PasswordChangeException(String message) {
            super(message);
        }
    }
}