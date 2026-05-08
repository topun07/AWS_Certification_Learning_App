package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.AppUser;
import com.example.awsMachineLearningExam.repository.AppUserRepository;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final AppUserRepository userRepository;

    @org.springframework.beans.factory.annotation.Value("${app.cors.allowed-origin:http://localhost:5173}")
    private String appOrigin;

    public PaymentController(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // --- 🛒 STEP 1: CREATE THE SECURE CHECKOUT SESSION ---
    @PostMapping("/checkout")
    public ResponseEntity<?> createCheckoutSession(Principal principal, @RequestBody(required = false) String rawBody) {
        try {
            // 1. Identify the user securely via Spring Security
            String identifier = principal.getName();
            AppUser user = userRepository.findByUsername(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found in database"));

            // 2. Prevent double-billing
            if (user.isPremium()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "User is already a Premium member."));
            }

            // Parse planType from JSON body
            String planType = "trial";
            if (rawBody != null && !rawBody.isBlank()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(rawBody);
                    if (node.has("planType") && !node.get("planType").isNull()) {
                        planType = node.get("planType").asText();
                    }
                } catch (Exception ignored) {}
            }
            System.out.println("📡 CHECKOUT REQUEST - Plan Type: [" + planType + "]");

            // Stripe Price IDs
            final String PRICE_ANNUAL = "price_1TUuaf9cwza92vUtR93URo4b";   // $79.99/year
            final String PRICE_MONTHLY = "price_1TUucy9cwza92vUtiTlV9gHV";  // $9.99/month
            final String PRICE_TRIAL = "price_1TUuZh9cwza92vUtMxRZMOca";    // $1 trial

            SessionCreateParams params;

            if ("annual".equals(planType)) {
                // ANNUAL PLAN: $79.99/year
                params = SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setSuccessUrl(appOrigin + "/?success=true")
                        .setCancelUrl(appOrigin + "/?canceled=true")
                        .setClientReferenceId(user.getUsername())
                        .setCustomerEmail(user.getEmail())
                        .addLineItem(SessionCreateParams.LineItem.builder()
                                .setPrice(PRICE_ANNUAL)
                                .setQuantity(1L)
                                .build())
                        .build();

            } else if ("monthly".equals(planType)) {
                // MONTHLY PLAN: $9.99/mo
                params = SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setSuccessUrl(appOrigin + "/?success=true")
                        .setCancelUrl(appOrigin + "/?canceled=true")
                        .setClientReferenceId(user.getUsername())
                        .setCustomerEmail(user.getEmail())
                        .addLineItem(SessionCreateParams.LineItem.builder()
                                .setPrice(PRICE_MONTHLY)
                                .setQuantity(1L)
                                .build())
                        .build();

            } else {
                // TRIAL PLAN: $1 today + $9.99/mo after 7 days
                params = SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setSuccessUrl(appOrigin + "/?success=true")
                        .setCancelUrl(appOrigin + "/?canceled=true")
                        .setClientReferenceId(user.getUsername())
                        .setCustomerEmail(user.getEmail())
                        .addLineItem(SessionCreateParams.LineItem.builder()
                                .setPrice(PRICE_TRIAL)
                                .setQuantity(1L)
                                .build())
                        .addLineItem(SessionCreateParams.LineItem.builder()
                                .setPrice(PRICE_MONTHLY)
                                .setQuantity(1L)
                                .build())
                        .setSubscriptionData(
                                SessionCreateParams.SubscriptionData.builder()
                                        .setTrialPeriodDays(7L)
                                        .build()
                        )
                        .build();
            }

            Session session = Session.create(params);

            return ResponseEntity.ok(Map.of("checkoutUrl", session.getUrl()));

        } catch (Exception e) {
            System.out.println("Checkout Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to initialize secure checkout."));
        }
    }
}