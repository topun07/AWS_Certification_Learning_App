package com.example.awsMachineLearningExam.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // Spring Boot automatically injects the mail configuration we set up earlier!
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendTestEmail(String targetEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        
        // 🚨 CRITICAL: This must exactly match the email address in your application.properties!
        message.setFrom("YOUR_NEW_WORKSPACE_EMAIL@knowledgeforgeacademy.com"); 
        
        message.setTo(targetEmail);
        message.setSubject("Initiation Complete: Welcome to the Forge");
        message.setText("If you are reading this, the Knowledge Forge comms pipeline is fully operational.\n\nYour Spring Boot backend successfully connected to the Google Workspace SMTP server, authenticated via App Passwords, and routed this message through AWS DNS.\n\nReady to build.");

        // Fire the email
        mailSender.send(message);
        System.out.println("✅ Comms Pipeline: Email successfully fired to " + targetEmail);
    }
}