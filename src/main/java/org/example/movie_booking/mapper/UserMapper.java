package org.example.movie_booking.mapper;

import org.example.movie_booking.model.dto.RegisterRequest;
import org.example.movie_booking.model.dto.UserResponse;
import org.example.movie_booking.model.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(RegisterRequest request) {
        return User.builder()
                .name(request.name())
                .username(request.username())
                .email(request.email())
                .password(request.password())
                .build();
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getUsername(), user.getEmail());
    }
}
