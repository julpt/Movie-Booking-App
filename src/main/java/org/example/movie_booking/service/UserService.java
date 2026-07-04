package org.example.movie_booking.service;

import lombok.RequiredArgsConstructor;
import org.example.movie_booking.exceptions.MovieNotFoundException;
import org.example.movie_booking.exceptions.UserNotFoundException;
import org.example.movie_booking.mapper.UserMapper;
import org.example.movie_booking.model.dto.MovieResponse;
import org.example.movie_booking.model.dto.UserResponse;
import org.example.movie_booking.model.entities.Movie;
import org.example.movie_booking.model.entities.User;
import org.example.movie_booking.repository.MovieRepository;
import org.example.movie_booking.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final UserMapper userMapper;

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return userMapper.toResponse(user);
    }

    @Transactional
    public void addFavoriteMovie(String username, Long movieId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        if (!user.getFavoriteMovies().contains(movie)) {
            user.getFavoriteMovies().add(movie);
            userRepository.save(user);
            log.info("User {} added movie {} to favorites", username, movieId);
        }
    }

    @Transactional
    public void removeFavoriteMovie(String username, Long movieId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        user.getFavoriteMovies().removeIf(m -> m.getId().equals(movieId));
        userRepository.save(user);
        log.info("User {} removed movie {} from favorites", username, movieId);
    }

    public List<MovieResponse> getFavoriteMovies(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return user.getFavoriteMovies().stream()
                .map(m -> new MovieResponse(m.getId(), m.getTitle(), m.getDescription(),
                        m.getDurationMinutes(), m.getGenre(), m.getRating(), m.getReleaseDate()))
                .collect(Collectors.toList());
    }

    public boolean isFavorite(String username, Long movieId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return user.getFavoriteMovies().stream().anyMatch(m -> m.getId().equals(movieId));
    }
}