package com.example.studentmonitor.service;

import com.example.studentmonitor.dto.SignupRequest;
import com.example.studentmonitor.model.User;
import com.example.studentmonitor.repository.UserRepository;
import com.example.studentmonitor.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;

    @InjectMocks AuthServiceImpl authService;

    @Test
    void signup_shouldSaveUser_whenEmailUnique() {
        SignupRequest req = new SignupRequest("alice@example.com", "secret123");

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });

        User saved = authService.signup(req);

        assertNotNull(saved.getId());
        assertEquals("alice@example.com", saved.getEmail());
    }

    @Test
    void signup_shouldThrowException_whenEmailExists() {
        SignupRequest req = new SignupRequest("bob@example.com", "password");

        when(userRepository.existsByEmail("bob@example.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.signup(req));

        assertEquals("Email already taken", ex.getMessage());
    }
}