package com.khoipd8.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Password encoder bean using BCrypt hashing algorithm
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Security filter chain configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // API documentation
                        //.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Test
                        .anyRequest().permitAll()

                        // Public endpoints
                        //.requestMatchers("/api/v1/auth/**").permitAll()
                        //.requestMatchers("/api/v1/users/register").permitAll()
                        //.requestMatchers("/api/v1/public/**").permitAll()

                        // H2 Console (for development only)
                        //.requestMatchers("/h2-console/**").permitAll()

                        // Actuator endpoints
                        //.requestMatchers("/actuator/health").permitAll()
                        //.requestMatchers("/actuator/**").hasRole("ADMIN")

                        // Admin endpoints
                        //.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // User management endpoints
                        //.requestMatchers("/api/v1/users/**").authenticated()

                        // All other requests require authentication
                        // .anyRequest().authenticated()
                )
                // Simple headers configuration - just fix the frame options deprecation
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // Allow H2 console
                );

        return http.build();
    }

    /**
     * CORS configuration
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}