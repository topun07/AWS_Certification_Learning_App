package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.AppUser;
import com.example.awsMachineLearningExam.repository.AppUserRepository;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    // 🚨 1. Inject the database so we can check if they are already Premium
    @Autowired
    private AppUserRepository userRepository;

    @PostMapping("/checkout")
    public ResponseEntity<?> createCheckoutSession(Principal principal) {
        try {
            // 1. Grab the identifier (username) from the JWT token
            String identifier = principal.getName();

            // 2. Fetch the actual user from the database
            AppUser user = userRepository.findByUsername(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found in database"));

            // 3. THE FIREWALL: If they are already Premium, BLOCK THE CHECKOUT!
            if (user.isPremium()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "User is already a Premium member."));
            }

            // 4. Build the Stripe Checkout Session
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setSuccessUrl("http://localhost:5173/?success=true")
                    .setCancelUrl("http://localhost:5173/?canceled=true")

                    // 🚨 THE TRACKING BEACON: We use user.getEmail() here!
                    .setClientReferenceId(user.getEmail())

                    // Lock Stripe to this Email so they can't double-subscribe
                    .setCustomerEmail(user.getEmail())

                    // 👇 PUT YOUR .addLineItem() CODE RIGHT HERE! 👇
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    // Make sure you have your actual Stripe Price ID here!
                                    .setPrice("price_1TOKyh9cwza92vUtmi0BzEAV")
                                    .build()
                    )
                    .build();

            // Create the session in Stripe
            Session session = Session.create(params);

            // Send the URL back to Vue so it can redirect the user
            return ResponseEntity.ok(Map.of("checkoutUrl", session.getUrl()));

        } catch (Exception e) {
            System.out.println("Checkout Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to initialize secure checkout."));
        }
    }
}