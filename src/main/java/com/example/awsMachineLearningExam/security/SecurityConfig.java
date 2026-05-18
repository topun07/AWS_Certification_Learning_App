package com.example.awsMachineLearningExam.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @org.springframework.beans.factory.annotation.Value("${app.cors.allowed-origin:http://localhost:5173}")
    private String corsOrigin;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. DISABLE CSRF
                .csrf(csrf -> csrf.disable())

                // 🚨 2. ENABLE CORS (Properly linked to your Bean below)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 🚨 3. STATELESS ARCHITECTURE: Tell Spring NOT to track server sessions.
                // This is absolutely mandatory for JWT-based REST APIs!
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. OPEN THE FRONT DOORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/questions/public/reviews").permitAll()
                        .requestMatchers("/api/questions/random").permitAll()
                        .requestMatchers("/api/questions/flashcards/game").permitAll()
                        .requestMatchers("/api/questions/pipeline/random").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/users/login", "/api/users/register").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/questions/**").permitAll()
                        .requestMatchers("/api/auth/login", "/api/users/register").permitAll()
                        .requestMatchers("/api/questions/pipeline/upload").permitAll()
                        .requestMatchers("/api/questions/upload").permitAll()
                        .requestMatchers("/api/questions/flashcards/upload").permitAll()
                        .requestMatchers("/api/questions/public/reviews").permitAll()
                        .requestMatchers("/api/auth/**", "/api/study/**").permitAll()
                        .requestMatchers("/api/analytics/**").permitAll()
                        .requestMatchers("/api/ai/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/api/auth/**", "/api/study/**", "/api/email/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 🚨 5. PLUG IN THE FILTER: Put your custom JWT bouncer exactly where it belongs
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(corsOrigin, "http://localhost:5173", "http://localhost:5174"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}