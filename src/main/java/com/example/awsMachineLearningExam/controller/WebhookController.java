package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.AppUser;
import com.example.awsMachineLearningExam.repository.AppUserRepository;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment") // 🚨 Aligned with your payment ecosystem
public class WebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Autowired
    private AppUserRepository userRepository;

    @PostMapping("/webhook") // 🚨 The final URL is now /api/payment/webhook
    public ResponseEntity<?> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().deserializeUnsafe();

                // READ THE BEACON (The Email)
                String emailBeacon = session.getClientReferenceId();
                System.out.println("📡 [WEBHOOK] Received Stripe receipt for Email: " + emailBeacon);

                if (emailBeacon != null) {
                    AppUser user = userRepository.findByUsername(emailBeacon).orElse(null);

                    if (user != null) {
                        user.setPremium(true);
                        // Mark trial as used
                        user.setHasUsedTrial(true);
                        // Save Stripe subscription and customer IDs for cancellation
                        if (session.getSubscription() != null) {
                            user.setStripeSubscriptionId(session.getSubscription());
                        }
                        if (session.getCustomer() != null) {
                            user.setStripeCustomerId(session.getCustomer());
                        }
                        userRepository.save(user);
                        System.out.println("✅ [WEBHOOK SECURE] UPGRADED USER TO PREMIUM: " + emailBeacon);
                    } else {
                        System.out.println("❌ [WEBHOOK ERROR] Database rejected receipt. No user found with username: " + emailBeacon);
                    }
                } else {
                    System.out.println("❌ [WEBHOOK ERROR] Stripe receipt arrived with a blank Tracking Beacon!");
                }
            }
            return ResponseEntity.ok("Webhook Processed");

        } catch (Exception e) {
            System.out.println("🚨 [WEBHOOK SECURITY BREACH] " + e.getMessage());
            return ResponseEntity.badRequest().body("Webhook Error");
        }
    }
}