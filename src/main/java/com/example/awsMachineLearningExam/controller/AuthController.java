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
    public ResponseEntity<?> registerUser(@RequestBody AppUser newUser) {

        // 1. FORMAT CHECK: Does it actually look like an email?
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!newUser.getEmail().matches(emailRegex)) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Invalid email format. Access Denied."));
        }

        // 2. DUPLICATE CHECK: Is the email already in the database?
        if (userRepository.existsByEmail(newUser.getEmail())) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Email is already registered in the Matrix."));
        }

        // 3. USERNAME CHECK: Is the username taken?
        if (userRepository.existsByUsername(newUser.getUsername())) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Username is already taken by another recruit."));
        }

        // 4. If they pass all checks, save them!
        // (Make sure you are still hashing their password here if you have Spring Security set up!)
        AppUser savedUser = userRepository.save(newUser);
        return ResponseEntity.ok(savedUser);
    }

    public record RegisterRequest(String username, String email, String password) {}
    public record LoginRequest(String email, String password) {}
}