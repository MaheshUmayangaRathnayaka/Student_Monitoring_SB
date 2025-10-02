package com.example.studentmonitor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.studentmonitor.dto.SignupRequest;
import com.example.studentmonitor.model.User;
import com.example.studentmonitor.repository.UserRepository;
import com.example.studentmonitor.service.impl.AuthServiceImpl;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock 
    private UserRepository userRepository;

    @InjectMocks 
    private AuthServiceImpl authService;

    private SignupRequest validRequest;
    private SignupRequest duplicateRequest;

    @BeforeEach
    void setUp() {
        validRequest = new SignupRequest("alice@example.com", "secret123");
        duplicateRequest = new SignupRequest("bob@example.com", "password");
    }

    @Test
    void signup_shouldSaveUser_whenEmailIsUnique() {
        // Arrange
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        // Act
        User saved = authService.signup(validRequest);

        // Assert
        assertNotNull(saved.getId(), "Saved user should have an ID");
        assertEquals(validRequest.getEmail(), saved.getEmail(), "Saved user email should match request");
    }

    @Test
    void signup_shouldThrowException_whenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(duplicateRequest.getEmail())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.signup(duplicateRequest));

        assertEquals("Email already taken", ex.getMessage());
    }
}
