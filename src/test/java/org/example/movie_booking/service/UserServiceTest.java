package org.example.movie_booking.service;

import org.example.movie_booking.exceptions.MovieNotFoundException;
import org.example.movie_booking.exceptions.UserNotFoundException;
import org.example.movie_booking.mapper.UserMapper;
import org.example.movie_booking.model.dto.MovieResponse;
import org.example.movie_booking.model.dto.UpdateProfileRequest;
import org.example.movie_booking.model.dto.UserResponse;
import org.example.movie_booking.model.entities.Movie;
import org.example.movie_booking.model.entities.User;
import org.example.movie_booking.repository.MovieRepository;
import org.example.movie_booking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void whenUserExists_getUserByUsername_returnsUserResponse() {
        User user = User.builder().id(1L).username("ion").name("Ion").email("ioio@test.com").build();
        UserResponse response = new UserResponse(1L, "Ion", "ion", "ioio@test.com");

        when(userRepository.findByUsername("ion")).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.getUserByUsername("ion");

        assertNotNull(result);
        assertEquals("ion", result.username());
        verify(userRepository).findByUsername("ion");
    }

    @Test
    void whenUserNotFound_getUserByUsername_throwsException() {
        when(userRepository.findByUsername("absent")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("absent"));
    }

    @Test
    void whenMovieNotAlreadyFavorite_addFavoriteMovie_addsSuccessfully() {
        User user = User.builder().id(1L).username("ion").favoriteMovies(new ArrayList<>()).build();
        Movie movie = Movie.builder().id(10L).title("Dune").build();

        when(userRepository.findByUsername("ion")).thenReturn(Optional.of(user));
        when(movieRepository.findById(10L)).thenReturn(Optional.of(movie));

        userService.addFavoriteMovie("ion", 10L);

        assertEquals(1, user.getFavoriteMovies().size());
        assertTrue(user.getFavoriteMovies().contains(movie));
        verify(userRepository).save(user);
    }

    @Test
    void whenMovieAlreadyFavorite_addFavoriteMovie_doesNotDuplicate() {
        Movie movie = Movie.builder().id(10L).title("Dune").build();
        User user = User.builder().id(1L).username("ion")
                .favoriteMovies(new ArrayList<>(List.of(movie))).build();

        when(userRepository.findByUsername("ion")).thenReturn(Optional.of(user));
        when(movieRepository.findById(10L)).thenReturn(Optional.of(movie));

        userService.addFavoriteMovie("ion", 10L);

        assertEquals(1, user.getFavoriteMovies().size()); // verifica duplicare
        verify(userRepository, never()).save(any());
    }

    @Test
    void whenMovieNotFound_addFavoriteMovie_throwsException() {
        User user = User.builder().id(1L).username("ion").build();
        when(userRepository.findByUsername("ion")).thenReturn(Optional.of(user));
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> userService.addFavoriteMovie("ion", 99L));
    }

    @Test
    void removeFavoriteMovie_removesMovieFromList() {
        Movie movie = Movie.builder().id(10L).title("Dune").build();
        User user = User.builder().id(1L).username("ion")
                .favoriteMovies(new ArrayList<>(List.of(movie))).build();

        when(userRepository.findByUsername("ion")).thenReturn(Optional.of(user));

        userService.removeFavoriteMovie("ion", 10L);

        assertEquals(0, user.getFavoriteMovies().size());
        verify(userRepository).save(user);
    }

    @Test
    void getFavoriteMovies_returnsMappedList() {
        Movie movie = Movie.builder().id(10L).title("Dune").genre(null).build();
        User user = User.builder().id(1L).username("ion")
                .favoriteMovies(new ArrayList<>(List.of(movie))).build();

        when(userRepository.findByUsername("ion")).thenReturn(Optional.of(user));

        List<MovieResponse> result = userService.getFavoriteMovies("ion");

        assertEquals(1, result.size());
        assertEquals("Dune", result.get(0).title());
    }

    @Test
    void isFavorite_whenMovieInList_returnsTrue() {
        Movie movie = Movie.builder().id(10L).title("Dune").build();
        User user = User.builder().id(1L).username("ion")
                .favoriteMovies(new ArrayList<>(List.of(movie))).build();

        when(userRepository.findByUsername("ion")).thenReturn(Optional.of(user));

        assertTrue(userService.isFavorite("ion", 10L));
    }

    @Test
    void isFavorite_whenMovieNotInList_returnsFalse() {
        User user = User.builder().id(1L).username("ion").favoriteMovies(new ArrayList<>()).build();
        when(userRepository.findByUsername("ion")).thenReturn(Optional.of(user));

        assertFalse(userService.isFavorite("ion", 10L));
    }

    @Test
    void whenUserExists_updateProfile_updatesFieldsAndReturns() {
        User user = User.builder().id(1L).username("user").name("Nume Vechi").email("old@test.com").build();
        UpdateProfileRequest request = new UpdateProfileRequest("Nume Nou", "new@test.com");
        UserResponse response = new UserResponse(1L, "Nume Nou", "user", "new@test.com");

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.updateProfile("user", request);

        assertEquals("Nume Nou", result.name());
        assertEquals("new@test.com", result.email());
        verify(userRepository).save(user);
    }

    @Test
    void whenUserNotFound_updateProfile_throwsException() {
        UpdateProfileRequest request = new UpdateProfileRequest("Nume", "email@test.com");
        when(userRepository.findByUsername("absent")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateProfile("absent", request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void whenUserExists_deleteUser_deletesSuccessfully() {
        User user = User.builder().id(1L).username("user").build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        userService.deleteUser("user");

        verify(userRepository).delete(user);
    }

    @Test
    void whenUserNotFound_deleteUser_throwsException() {
        when(userRepository.findByUsername("absent")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser("absent"));
        verify(userRepository, never()).delete(any());
    }
}