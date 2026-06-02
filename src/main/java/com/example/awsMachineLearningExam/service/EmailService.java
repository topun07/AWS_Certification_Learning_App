package com.example.awsMachineLearningExam.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@knowledgeforgeacademy.com}")
    private String fromEmail;

    @Value("${app.admin.email:aaron@knowledgeforgeacademy.com}")
    private String adminEmail;

    @Value("${mailchimp.api.key:}")
    private String mailchimpApiKey;

    @Value("${mailchimp.audience.id:}")
    private String mailchimpAudienceId;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a test email — used by EmailController for debugging.
     */
    @Async
    public void sendTestEmail(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Knowledge Forge Academy - Test Email");
        message.setText("This is a test email from Knowledge Forge Academy. If you received this, SES is working correctly!");
        mailSender.send(message);
    }

    /**
     * Sends a welcome email to the new user.
     */
    @Async
    public void sendWelcomeEmail(String toEmail, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to Knowledge Forge Academy, " + username + "!");
            message.setText(
                "Hey " + username + ",\n\n" +
                "Welcome to Knowledge Forge Academy! Your account is ready.\n\n" +
                "Here's what you can do right now for FREE:\n" +
                "  - Sudden Death mode (unlimited)\n" +
                "  - Knowledge Forge library\n\n" +
                "Upgrade to Premium to unlock:\n" +
                "  - Simulated Exams (65 questions)\n" +
                "  - AMRAP Attack\n" +
                "  - Flashcard Matrix\n" +
                "  - Study Cards & Architecture Matrix\n\n" +
                "Start training: https://knowledgeforgeacademy.com\n\n" +
                "Good luck on your certification journey!\n\n" +
                "— The Knowledge Forge Team"
            );
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send welcome email to " + toEmail + ": " + e.getMessage());
        }
    }

    /**
     * Notifies the admin of a new signup.
     */
    @Async
    public void sendAdminSignupNotification(String newUserEmail, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(adminEmail);
            message.setSubject("New Recruit: " + username);
            message.setText(
                "A new user has joined Knowledge Forge Academy!\n\n" +
                "Username : " + username + "\n" +
                "Email    : " + newUserEmail + "\n\n" +
                "View all users in your admin dashboard."
            );
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send admin notification: " + e.getMessage());
        }
    }

    /**
     * Adds the new user to the Mailchimp audience.
     */
    @Async
    public void addToMailchimp(String email, String username) {
        if (mailchimpApiKey == null || mailchimpApiKey.isBlank() ||
            mailchimpAudienceId == null || mailchimpAudienceId.isBlank()) {
            System.out.println("Mailchimp not configured, skipping.");
            return;
        }

        try {
            // Extract datacenter from API key (e.g. "us2" from "xxx-us2")
            String dc = mailchimpApiKey.substring(mailchimpApiKey.lastIndexOf('-') + 1);
            String url = "https://" + dc + ".api.mailchimp.com/3.0/lists/" + mailchimpAudienceId + "/members";

            String body = String.format(
                "{\"email_address\":\"%s\",\"status\":\"subscribed\",\"merge_fields\":{\"FNAME\":\"%s\"}}",
                email, username
            );

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + mailchimpApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                System.out.println("Added " + email + " to Mailchimp.");
            } else {
                System.err.println("Mailchimp error " + response.statusCode() + ": " + response.body());
            }
        } catch (Exception e) {
            System.err.println("Failed to add to Mailchimp: " + e.getMessage());
        }
    }
}
