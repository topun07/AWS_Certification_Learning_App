package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.AppUser;
import com.example.awsMachineLearningExam.repository.AppUserRepository;
import com.example.awsMachineLearningExam.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, AppUserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request) {
        // Look up by email
        AppUser user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Access Denied: Email not found."));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.password())
        );

        String token = jwtService.generateToken(user.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("xp", user.getXp());
        response.put("isPremium", user.isPremium());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        AppUser user = new AppUser(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password())
        );

        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("xp", user.getXp());
        response.put("isPremium", user.isPremium());

        return ResponseEntity.ok(response);
    }

    public record RegisterRequest(String username, String email, String password) {}
    public record LoginRequest(String email, String password) {}
}