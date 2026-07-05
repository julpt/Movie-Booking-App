package org.example.movie_booking.service;

import org.example.movie_booking.exceptions.InvalidCredentialsException;
import org.example.movie_booking.mapper.UserMapper;
import org.example.movie_booking.model.dto.LoginRequest;
import org.example.movie_booking.model.dto.UserResponse;
import org.example.movie_booking.model.entities.User;
import org.example.movie_booking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void whenCredentialsValid_login_returnsUserResponse() {
        LoginRequest request = new LoginRequest("user", "plainPassword");
        User user = User.builder().id(1L).username("user").password("hashedPassword").build();
        UserResponse response = new UserResponse(1L, "Ion Ionescu", "user", "ion@test.com");

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plainPassword", "hashedPassword")).thenReturn(true);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = authService.login(request);

        assertNotNull(result);
        assertEquals("user", result.username());
        verify(userRepository).findByUsername("user");
        verify(passwordEncoder).matches("plainPassword", "hashedPassword");
    }

    @Test
    void whenUserNotFound_login_throwsInvalidCredentialsException() {
        LoginRequest request = new LoginRequest("absent", "parola");
        when(userRepository.findByUsername("absent")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void whenPasswordIncorrect_login_throwsInvalidCredentialsException() {
        LoginRequest request = new LoginRequest("user1", "wrongPassword");
        User user = User.builder().id(1L).username("user1").password("hashedPassword").build();

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verify(userMapper, never()).toResponse(any());
    }
}