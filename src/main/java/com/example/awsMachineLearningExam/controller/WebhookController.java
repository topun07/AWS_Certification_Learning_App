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
@RequestMapping("/api/webhook")
public class WebhookController {

    // This is a special password just for Webhooks so hackers can't fake payment calls
    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Autowired
    private AppUserRepository userRepository;

    @PostMapping("/stripe")
    public ResponseEntity<?> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().deserializeUnsafe();

                // 🚨 1. READ THE BEACON (This is now the Email!)
                String emailBeacon = session.getClientReferenceId();
                System.out.println("📡 [WEBHOOK] Received Stripe receipt for Email: " + emailBeacon);

                // 🚨 2. SEARCH BY EMAIL, NOT USERNAME!
                if (emailBeacon != null) {
                    AppUser user = userRepository.findByEmail(emailBeacon).orElse(null);

                    if (user != null) {
                        user.setPremium(true);
                        userRepository.save(user);
                        System.out.println("✅ [WEBHOOK SECURE] UPGRADED USER TO PREMIUM: " + emailBeacon);
                    } else {
                        System.out.println("❌ [WEBHOOK ERROR] Database rejected receipt. No user found with email: " + emailBeacon);
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