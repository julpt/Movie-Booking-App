package org.example.movie_booking.config;

import lombok.RequiredArgsConstructor;
import org.example.movie_booking.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // USER autentificat - booking-uri proprii, cinematografe
                        .requestMatchers("/bookings/**", "/api/bookings/**", "/cinemas", "/profile", "/movies/{id}/favorite", "/movies/{id}/unfavorite").authenticated()

                        // ADMIN
                        .requestMatchers("/movies/new", "/movies/edit/**", "/movies/delete/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/movies","/api/movies").hasRole("ADMIN")
                        .requestMatchers("/cinemas/**").hasRole("ADMIN")
                        .requestMatchers("/api/cinemas/**", "/api/screens/**").hasRole("ADMIN")
                        .requestMatchers("/api/payments/**").hasRole("ADMIN")

                        // PUBLIC
                        .requestMatchers("/webjars/**", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/login", "/register", "/error", "/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/movies", "/movies/{id}/*").permitAll()  // doar READ pe movies e public
                        .requestMatchers(HttpMethod.GET, "/api/movies/**").permitAll()

                        // Orice altceva - REFUZAT by default
                        .anyRequest().denyAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/movies", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key("movieBookingRememberMeKey")
                        .tokenValiditySeconds(86400) // o zi
                );

        return http.build();
    }
}