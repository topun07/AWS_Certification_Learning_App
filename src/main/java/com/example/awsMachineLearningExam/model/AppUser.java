package com.example.awsMachineLearningExam.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_premium")
    private boolean isPremium = false;

    @Column(name = "pending_cancellation")
    private boolean pendingCancellation = false;

    @Column(name = "has_used_trial")
    private boolean hasUsedTrial = false;

    @Column(name = "had_trial")
    private boolean hadTrial = false;

    @Column(name = "stripe_subscription_id")
    private String stripeSubscriptionId;

    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    @Column(name = "premium_expiration_date")
    private java.time.LocalDate premiumExpirationDate;

    private int xp;
    private String rankTitle;

    // Make sure these are the ONLY streak/date variables!
    private int currentStreak = 0;
    private LocalDate lastStudyDate;

    // --- Constructors ---
    public AppUser() {}

    public AppUser(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.xp = 0; // Everyone starts at zero
        this.rankTitle = "Initiate";
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }
    public String getRankTitle() { return rankTitle; }
    public void setRankTitle(String rankTitle) { this.rankTitle = rankTitle; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public LocalDate getLastStudyDate() { return lastStudyDate; }
    public void setLastStudyDate(LocalDate lastStudyDate) { this.lastStudyDate = lastStudyDate; }
    public boolean isPremium() { return isPremium; }
    public void setPremium(boolean premium) { isPremium = premium; }
    public boolean isPendingCancellation() { return pendingCancellation; }
    public void setPendingCancellation(boolean pendingCancellation) { this.pendingCancellation = pendingCancellation; }
    public boolean isHadTrial() { return hadTrial; }
    public void setHadTrial(boolean hadTrial) { this.hadTrial = hadTrial; }
    public java.time.LocalDate getPremiumExpirationDate() { return premiumExpirationDate; }
    public void setPremiumExpirationDate(java.time.LocalDate premiumExpirationDate) { this.premiumExpirationDate = premiumExpirationDate; }
    public String getStripeSubscriptionId() { return stripeSubscriptionId; }
    public void setStripeSubscriptionId(String stripeSubscriptionId) { this.stripeSubscriptionId = stripeSubscriptionId; }
    public String getStripeCustomerId() { return stripeCustomerId; }
    public void setStripeCustomerId(String stripeCustomerId) { this.stripeCustomerId = stripeCustomerId; }
    public boolean isHasUsedTrial() { return hasUsedTrial; }
    public void setHasUsedTrial(boolean hasUsedTrial) { this.hasUsedTrial = hasUsedTrial; }
    }