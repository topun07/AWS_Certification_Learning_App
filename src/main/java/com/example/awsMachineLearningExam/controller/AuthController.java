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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        // 1. Find the user by their email
        User user = userRepository.findByEmail(request.email);

        // 2. If no user is found with that email, reject the request
        if (user == null) {
            return ResponseEntity.status(401).body("Error: Email not found.");
        }

        // 3. Verify the password matches the hashed version in the database
        if (passwordEncoder.matches(request.passwordhash, user.getPasswordHash())) {
            // Success! Return the user object to Vue (which saves it in localStorage)
            return ResponseEntity.ok(user);
        } else {
            // Passwords do not match
            return ResponseEntity.status(401).body("Error: Invalid password.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User newUser) {

        // 1. Check if email is taken
        if (userRepository.findByEmail(newUser.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Error: Email is already registered.");
        }

        // 2. THE NEW FIX: Check if username is taken
        if (userRepository.findByUsername(newUser.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Error: Username is already taken.");
        }

        // 3. Hash the password and save!
        String plainTextPassword = newUser.getPasswordHash();
        String securelyHashedPassword = passwordEncoder.encode(plainTextPassword);
        newUser.setPasswordHash(securelyHashedPassword);

        User savedUser = userRepository.save(newUser);
        return ResponseEntity.ok(savedUser);
    }
}