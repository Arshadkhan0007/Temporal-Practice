package com.example.TemporalPractice.workflow;

import com.example.TemporalPractice.enums.SubscriptionStatus;
import com.example.TemporalPractice.activity.SubscriptionActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
@WorkflowImpl(taskQueues = "SUBSCRIPTION_TASK_QUEUE")
public class SubscriptionWorkflowImpl implements SubscriptionWorkflow {

    private boolean isCancelled = false;

    private SubscriptionStatus status = SubscriptionStatus.INITIALIZING;

    private final SubscriptionActivities activities = Workflow.newActivityStub(
            SubscriptionActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setMaximumAttempts(5)
                            .build())
                    .build()
    );

    @Override
    public void startSubscription(String userId, String email) {

        Saga saga = new Saga(new Saga.Options.Builder().setParallelCompensation(false).build());

        try {

            status = SubscriptionStatus.UPDATING_DATABASE;
            activities.updateDbToActive(userId);
            // Register the compensation IMMEDIATELY after success
            saga.addCompensation(activities::undoUpdateDbToInactive, userId);

            status = SubscriptionStatus.WAITING_FOR_TRAIL_TO_END;
            // The Smart Sleep: Wait 10 seconds OR until isCancelled is true
            Workflow.await(Duration.ofSeconds(20), () -> isCancelled);

            // Decide what to do based on how we woke up
            if (isCancelled) {
                System.out.println("User cancelled during the trial period! Ending workflow early.");
                status = SubscriptionStatus.CANCELED_BY_USER;
                return; // Gracefully exit without sending the welcome email
            }

            status = SubscriptionStatus.SENDING_WELCOME_MAIL;
            activities.sendWelcomeEmail(email);

            status = SubscriptionStatus.CHARGING_CREDIT_CARD;
            activities.chargeCreditCard(userId);
            saga.addCompensation(activities::refundCreditCard, userId);

            status = SubscriptionStatus.COMPLETED;

        } catch (ActivityFailure e) {
            // If ANY activity fails and exhausts its retries, this block runs.
            log.error("Workflow failed! Initiating Saga Compensation...");

            // This automatically runs the compensations in REVERSE order!
            // It will refund the card, THEN undo the DB update.
            saga.compensate();

            // Rethrow to mark the workflow as failed
            throw e;
        }
    }

    @Override
    public void cancelSubscription() {
        this.isCancelled = true; // This instantly unblocks the Workflow.await()!
    }

    @Override
    public String getSubscriptionStatus() {
        return this.status.name();
    }

}
