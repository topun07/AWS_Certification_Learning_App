package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.AppUser;
import com.example.awsMachineLearningExam.repository.AppUserRepository;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
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

    // The colon at the end prevents Spring Boot from crashing on startup if the variable is missing
    @org.springframework.beans.factory.annotation.Value("${stripe.api.key:}")
    private String stripeApiKey;

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

            // 3. Prevent trial abuse - one trial per account ever
            if ("trial".equals(planType) && user.isHasUsedTrial()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Trial already used. Please choose Monthly or Annual."));
            }

            // 3. Set your Stripe Secret Key to authenticate the request
            if (stripeApiKey == null || stripeApiKey.isBlank()) {
                System.out.println("🚨 SERVER ERROR: Stripe API Key is missing from AWS Environment Variables!");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Server misconfiguration. Payment gateway offline."));
            }

            Stripe.apiKey = stripeApiKey;

            // Parse planType and useFounderDiscount from JSON body
            String planType = "trial";
            boolean useFounderDiscount = false;
            if (rawBody != null && !rawBody.isBlank()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(rawBody);
                    if (node.has("planType") && !node.get("planType").isNull()) {
                        planType = node.get("planType").asText();
                    }
                    if (node.has("useFounderDiscount") && node.get("useFounderDiscount").asBoolean()) {
                        useFounderDiscount = true;
                    }
                } catch (Exception ignored) {}
            }
            System.out.println("📡 CHECKOUT REQUEST - Plan Type: [" + planType + "] | Founder Discount: " + useFounderDiscount);

            // Stripe Price IDs
            final String PRICE_ANNUAL = "price_1TWzs19z0vfxVeWeJFAJIN4S";    // $79.99/year
            final String PRICE_MONTHLY = "price_1TWzqK9z0vfxVeWeixarvNVE";   // $9.99/month
            final String PRICE_TRIAL = "price_1TWzoX9z0vfxVeWeF1Ym4Ajr";     // $1 trial

            // Founder Promo Code IDs
            final String PROMO_MONTHLY = "promo_1TYqmm9z0vfxVeWeMOnqAqEp";  // 30% off monthly
            final String PROMO_ANNUAL = "promo_1TYqq09z0vfxVeWeNMYW76Pg";   // 50% off annual

            SessionCreateParams params;

            if ("annual".equals(planType)) {
                SessionCreateParams.Builder builder = SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setSuccessUrl(appOrigin + "/?success=true")
                        .setCancelUrl(appOrigin + "/?canceled=true")
                        .setClientReferenceId(user.getUsername())
                        .setCustomerEmail(user.getEmail())
                        .addLineItem(SessionCreateParams.LineItem.builder()
                                .setPrice(PRICE_ANNUAL)
                                .setQuantity(1L)
                                .build());
                if (useFounderDiscount) {
                    builder.addDiscount(SessionCreateParams.Discount.builder()
                            .setPromotionCode(PROMO_ANNUAL)
                            .build());
                }
                params = builder.build();

            } else if ("monthly".equals(planType)) {
                SessionCreateParams.Builder builder = SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setSuccessUrl(appOrigin + "/?success=true")
                        .setCancelUrl(appOrigin + "/?canceled=true")
                        .setClientReferenceId(user.getUsername())
                        .setCustomerEmail(user.getEmail())
                        .addLineItem(SessionCreateParams.LineItem.builder()
                                .setPrice(PRICE_MONTHLY)
                                .setQuantity(1L)
                                .build());
                if (useFounderDiscount) {
                    builder.addDiscount(SessionCreateParams.Discount.builder()
                            .setPromotionCode(PROMO_MONTHLY)
                            .build());
                }
                params = builder.build();

            } else {
                // TRIAL PLAN: $1 today + $9.99/mo after 7 days (no founder discount on trial)
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

    // Check founder spots remaining
    @GetMapping("/founder-spots")
    public ResponseEntity<?> getFounderSpots() {
        try {
            Stripe.apiKey = stripeApiKey;
            com.stripe.model.PromotionCode promoMonthly = com.stripe.model.PromotionCode.retrieve("promo_1TYqmm9z0vfxVeWeMOnqAqEp");
            com.stripe.model.PromotionCode promoAnnual = com.stripe.model.PromotionCode.retrieve("promo_1TYqq09z0vfxVeWeNMYW76Pg");
            long monthlyUsed = promoMonthly.getTimesRedeemed() != null ? promoMonthly.getTimesRedeemed() : 0;
            long annualUsed = promoAnnual.getTimesRedeemed() != null ? promoAnnual.getTimesRedeemed() : 0;
            long totalUsed = monthlyUsed + annualUsed;
            long spotsRemaining = Math.max(0, 100 - totalUsed);
            return ResponseEntity.ok(Map.of("spotsRemaining", spotsRemaining, "totalSpots", 100, "active", spotsRemaining > 0));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("spotsRemaining", 100, "totalSpots", 100, "active", true));
        }
    }
}