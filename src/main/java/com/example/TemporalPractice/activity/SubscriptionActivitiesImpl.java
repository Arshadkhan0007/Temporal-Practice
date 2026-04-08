package com.example.TemporalPractice.activity;

import io.temporal.spring.boot.ActivityImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ActivityImpl(taskQueues = "SUBSCRIPTION_TASK_QUEUE")
public class SubscriptionActivitiesImpl implements SubscriptionActivities {


    @Override
    public void updateDbToActive(String userId) {
        log.info("SETTING TO SUBSCRIPTION STATUS TO ACTIVE FOR USER WITH ID: {}", userId);
    }

    @Override
    public void undoUpdateDbToInactive(String userId) {
        log.info("SETTING TO SUBSCRIPTION STATUS TO IN-ACTIVE FOR USER WITH ID: {}", userId);
    }

    @Override
    public void sendWelcomeEmail(String email) {
        log.info("SENDING A WELCOME EMAIL TO {}", email);
    }

    @Override
    public void setupBillingProfile(String userId) {
        log.info("SETTING UP BILLING PROFILE FOR USER WITH ID: {}", userId);
    }

    @Override
    public void chargeCreditCard(String userId) {
//        log.info("INTENTIONALLY FUCKING UP WHILE CHARGING CREDIT CARD");
//        throw new RuntimeException("INTENTIONAL FUCK UP");

        log.info("CHARGING CREDIT CARD OF USER WITH ID: {}", userId);
    }

    @Override
    public void refundCreditCard(String userId) {
        log.info("REFUNDING CREDIT CARD OF USER WITH ID: {}", userId);
    }
}
