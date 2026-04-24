package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.AppUser;
import com.example.awsMachineLearningExam.repository.AppUserRepository;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.fasterxml.jackson.databind
        .JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    // ✅ Clean, single database injection
    private final AppUserRepository userRepository;

    public PaymentController(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // --- 🛒 STEP 1: CREATE THE CHECKOUT SESSION ---
    @PostMapping("/checkout")
    public ResponseEntity<?> createCheckoutSession(Principal principal) {
        try {
            String identifier = principal.getName();

            AppUser user = userRepository.findByUsername(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found in database"));

            if (user.isPremium()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "User is already a Premium member."));
            }

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setSuccessUrl("http://localhost:5173/?success=true")
                    .setCancelUrl("http://localhost:5173/?canceled=true")
                    // 🚨 MATCHED TO WEBHOOK: We are tracking them by their unique username
                    .setClientReferenceId(user.getUsername())
                    .setCustomerEmail(user.getEmail())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPrice("price_1TOKyh9cwza92vUtmi0BzEAV") // Your Stripe Price ID
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);

            return ResponseEntity.ok(Map.of("checkoutUrl", session.getUrl()));

        } catch (Exception e) {
            System.out.println("Checkout Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to initialize secure checkout."));
        }
    }

    // --- 🚨 STEP 2: CATCH THE WEBHOOK AND UPGRADE THE USER ---
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload) {
        try {
            System.out.println("🚨 INCOMING STRIPE WEBHOOK DETECTED!");

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(payload);
            String eventType = rootNode.path("type").asText();

            if ("checkout.session.completed".equals(eventType)) {

                JsonNode sessionNode = rootNode.path("data").path("object");

                // Extract the username we attached during checkout
                String username = sessionNode.path("client_reference_id").asText();

                System.out.println("✅ Payment Success for User: " + username);

                // Find the user and UPGRADE THEM
                userRepository.findByUsername(username).ifPresent(user -> {
                    user.setPremium(true);
                    userRepository.save(user);
                    System.out.println("🌟 USER " + username + " OFFICIALLY UPGRADED TO PREMIUM!");
                });
            }

            return ResponseEntity.ok("Webhook Received");

        } catch (Exception e) {
            System.err.println("❌ Webhook Parsing Error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Webhook Error");
        }
    }
}