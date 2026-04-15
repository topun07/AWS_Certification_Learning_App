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

    // We use constructor injection now instead of @Autowired (it's the modern standard!)
    public AuthController(AuthenticationManager authenticationManager, AppUserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request) {
        // 1. Let Spring Security's AuthenticationManager verify the hash
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        // 2. If successful, generate the VIP Pass!
        String token = jwtService.generateToken(request.username());

        // 3. Fetch the user to get their XP and stats
        AppUser user = userRepository.findByUsername(request.username()).orElseThrow();

        // 4. Return the Token instead of the raw User object
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", user.getUsername());
        response.put("xp", user.getXp());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        // 1. Check if email or username is taken
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        // 2. Create new user's account and encrypt their password BEFORE saving
        AppUser user = new AppUser(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password())
        );

        userRepository.save(user);

        // 3. Automatically log them in after registering by giving them a token
        String token = jwtService.generateToken(user.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", user.getUsername());
        response.put("xp", user.getXp());

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // DATA TRANSFER OBJECTS (DTOs)
    // ==========================================
    // This replaces your old AuthRequest file! It's much cleaner.
    public record RegisterRequest(String username, String email, String password) {}
    public record LoginRequest(String username, String password) {}
}