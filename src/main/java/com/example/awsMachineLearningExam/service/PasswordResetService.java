package com.example.awsMachineLearningExam.service;

import com.example.awsMachineLearningExam.model.AppUser;
import com.example.awsMachineLearningExam.model.PasswordResetToken;
import com.example.awsMachineLearningExam.repository.AppUserRepository;
import com.example.awsMachineLearningExam.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final AppUserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.cors.allowed-origin:https://knowledgeforgeacademy.com}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public PasswordResetService(AppUserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                JavaMailSender mailSender,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Generates a reset token and sends the email.
     * Always returns success to prevent email enumeration attacks.
     */
    @Transactional
    public void requestPasswordReset(String email) {
        Optional<AppUser> userOpt = userRepository.findByEmail(email);

        // If no user found, silently return — don't reveal whether email exists
        if (userOpt.isEmpty()) return;

        // Delete any existing tokens for this email before creating a new one
        tokenRepository.deleteByEmail(email);

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);

        PasswordResetToken resetToken = new PasswordResetToken(token, email, expiry);
        tokenRepository.save(resetToken);

        sendResetEmail(email, token);
    }

    /**
     * Validates the token and updates the password.
     */
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) return false;

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.isUsed() || resetToken.isExpired()) return false;

        Optional<AppUser> userOpt = userRepository.findByEmail(resetToken.getEmail());
        if (userOpt.isEmpty()) return false;

        AppUser user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return true;
    }

    private void sendResetEmail(String toEmail, String token) {
        String resetLink = frontendUrl + "/?reset_token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Knowledge Forge Academy - Password Reset");
        message.setText(
            "You requested a password reset for your Knowledge Forge Academy account.\n\n" +
            "Click the link below to reset your password (expires in 1 hour):\n\n" +
            resetLink + "\n\n" +
            "If you did not request this, you can safely ignore this email.\n\n" +
            "— The Knowledge Forge Team"
        );

        mailSender.send(message);
    }
}
