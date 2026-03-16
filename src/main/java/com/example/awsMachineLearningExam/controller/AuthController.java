package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.dto.AuthRequest;
import com.example.awsMachineLearningExam.model.User;
import com.example.awsMachineLearningExam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (userRepository.existsByEmail(request.email)) {
            return ResponseEntity.badRequest().body("Email already in use");
        }
        User user = new User();
        user.setEmail(request.email);
        user.setFullName(request.fullName);

        // HASH THE PASSWORD before saving
        user.setPasswordHash(passwordEncoder.encode(request.password));

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        // 1. Manually find the user
        java.util.Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        // 2. Use a standard if-statement instead of .map
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.status(401).body("User not found");
        }
    }

}