package com.example.awsMachineLearningExam.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.api.key}")
    private String secretKey;

    @PostConstruct
    public void initStripe() {
        // This fires up the Stripe Engine the second Spring Boot boots up!
        Stripe.apiKey = secretKey;
        System.out.println("💳 STRIPE ENGINE INITIALIZED. SECURE CONNECTION ESTABLISHED.");
    }
}