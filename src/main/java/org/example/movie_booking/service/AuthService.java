package org.example.movie_booking.service;

import lombok.RequiredArgsConstructor;
import org.example.movie_booking.exceptions.EmailAlreadyUsedException;
import org.example.movie_booking.exceptions.InvalidCredentialsException;
import org.example.movie_booking.exceptions.UsernameAlreadyUsedException;
import org.example.movie_booking.mapper.UserMapper;
import org.example.movie_booking.model.dto.LoginRequest;
import org.example.movie_booking.model.dto.RegisterRequest;
import org.example.movie_booking.model.dto.UserResponse;
import org.example.movie_booking.model.entities.Role;
import org.example.movie_booking.model.entities.User;
import org.example.movie_booking.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyUsedException(request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyUsedException(request.email());
        }
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER); // toti userii noi sunt USER by default

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return userMapper.toResponse(user);
    }
}