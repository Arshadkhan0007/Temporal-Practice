package com.example.TemporalPractice.activity;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface SubscriptionActivities {
    void updateDbToActive(String userId);
    void undoUpdateDbToInactive(String userId);
    void sendWelcomeEmail(String email);
    void setupBillingProfile(String userId);
    void chargeCreditCard(String userId);
    void refundCreditCard(String userId);
}
