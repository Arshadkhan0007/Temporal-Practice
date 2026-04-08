package com.example.TemporalPractice.workflow;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface SubscriptionWorkflow {
    @WorkflowMethod
    void startSubscription(String userId, String email);

    @SignalMethod
    void cancelSubscription();

    @QueryMethod
    String getSubscriptionStatus();
}
