package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.AppUser;
import com.example.awsMachineLearningExam.repository.AppUserRepository;
import com.example.awsMachineLearningExam.service.EmailService;
import com.example.awsMachineLearningExam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // 🚨 THE REGISTRATION ENDPOINT & ECHO TEST
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AppUser newUser) {

        // 1. THE ECHO TEST: Print exactly what Java received from Vue!
        System.out.println("===== INCOMING RECRUIT =====");
        System.out.println("USERNAME: " + newUser.getUsername());
        System.out.println("EMAIL: " + newUser.getEmail());
        System.out.println("============================");

        // 2. NULL CHECK & FORMAT CHECK
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (newUser.getEmail() == null || !newUser.getEmail().matches(emailRegex)) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Invalid email format. Access Denied."));
        }

        // 3. DUPLICATE CHECK: Is the email already in the database?
        if (userRepository.existsByEmail(newUser.getEmail())) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Email is already registered in the Matrix."));
        }

        // 4. USERNAME CHECK: Is the username taken?
        if (userRepository.existsByUsername(newUser.getUsername())) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Username is already taken by another recruit."));
        }

        String plainTextPassword = newUser.getPassword();
        String hashedPassword = passwordEncoder.encode(plainTextPassword);
        newUser.setPassword(hashedPassword);

        // 5. If they pass all checks, save them!
        AppUser savedUser = userRepository.save(newUser);

        // 6. Fire emails and Mailchimp async (non-blocking)
        emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getUsername());
        emailService.sendAdminSignupNotification(savedUser.getEmail(), savedUser.getUsername());
        emailService.addToMailchimp(savedUser.getEmail(), savedUser.getUsername());

        return ResponseEntity.ok(savedUser);
    }

    // 🚨 THE CANCELLATION ENDPOINT
    @PostMapping("/cancel-premium")
    public ResponseEntity<?> cancelPremiumMembership(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        Optional<AppUser> optionalUser = userRepository.findByUsername(principal.getName());

        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();

            // 1. Cancel in Stripe at period end (user keeps access until billing period ends)
            if (user.getStripeSubscriptionId() != null) {
                try {
                    com.stripe.model.Subscription subscription =
                        com.stripe.model.Subscription.retrieve(user.getStripeSubscriptionId());
                    com.stripe.param.SubscriptionUpdateParams params =
                        com.stripe.param.SubscriptionUpdateParams.builder()
                            .setCancelAtPeriodEnd(true)
                            .build();
                    subscription.update(params);
                    System.out.println("✅ Stripe subscription set to cancel at period end: " + user.getStripeSubscriptionId());
                } catch (Exception e) {
                    System.out.println("⚠️ Stripe cancellation error: " + e.getMessage());
                }
            }

            // 2. Mark as pending cancellation in our DB
            user.setPendingCancellation(true);

            // 3. Set expiration date if not already set
            if (user.getPremiumExpirationDate() == null) {
                user.setPremiumExpirationDate(java.time.LocalDate.now().with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()));
            }

            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "Cancellation scheduled. Access remains active until end of billing period.",
                    "expirationDate", user.getPremiumExpirationDate().toString()
            ));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "User not found."));
        }
    }

    // 2. The Missing Profile Endpoint!
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Principal principal) {
        // If there is no user logged in, reject it
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // Find the user in the database using their username
        Optional<AppUser> optionalUser = userRepository.findByUsername(principal.getName());

        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();

            // Package up the exact data Vue is asking for
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("username", user.getUsername());
            profileData.put("isPremium", user.isPremium()); // Grabs their premium status

            return ResponseEntity.ok(profileData);
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    // 🚨 THE MASTER SYNC BRIDGE: Allows Vue to request the official User ID by Username
     @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {

        // 🚨 THE FIX: Tell Java to look for AppUser, not User!
        java.util.Optional<AppUser> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{username}/record-study")
    public ResponseEntity<?> recordStudySession(@PathVariable String username) {
        try {
            AppUser updatedUser = userService.recordStudySession(username);
            return ResponseEntity.ok(Map.of(
                    "message", "Study session recorded.",
                    "currentStreak", updatedUser.getCurrentStreak()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update streak: " + e.getMessage());
        }
    }

    @PostMapping("/{username}/award-xp")
    public ResponseEntity<?> awardXp(@PathVariable String username, @RequestBody java.util.Map<String, Integer> payload) {
        try {
            int correctCount = payload.getOrDefault("correctCount", 0);
            int totalQuestions = payload.getOrDefault("totalQuestions", 1);
            int earnedXp = (correctCount * 10) + (correctCount == totalQuestions && totalQuestions > 0 ? 500 : 0);

            AppUser updatedUser = userService.awardXp(username, correctCount, totalQuestions);

            return ResponseEntity.ok(java.util.Map.of(
                    "message", "XP Awarded!",
                    "totalXp", updatedUser.getXp(),
                    "earnedXp", earnedXp
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to award XP: " + e.getMessage());
        }
    }
}