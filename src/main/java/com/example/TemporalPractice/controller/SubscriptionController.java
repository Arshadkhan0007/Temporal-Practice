package com.example.TemporalPractice.controller;

import com.example.TemporalPractice.workflow.SubscriptionWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubscriptionController {

    private final WorkflowClient workflowClient;

    public SubscriptionController(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam String userId, @RequestParam String email) {
        SubscriptionWorkflow workflow = workflowClient.newWorkflowStub(
                SubscriptionWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("SUBSCRIPTION_TASK_QUEUE")
                        .setWorkflowId("sub-" + userId) // Prevents duplicates stubs
                        .build());

        WorkflowClient.start(workflow::startSubscription, userId, email);
        return "Subscription Started!";
    }

    @PostMapping("/cancel")
    public String cancel(@RequestParam String userId) {
        // Create a stub pointing to the ALREADY RUNNING workflow
        // Notice we don't use WorkflowOptions here, just the exact Workflow ID!
        SubscriptionWorkflow workflow = workflowClient.newWorkflowStub(
                SubscriptionWorkflow.class,
                "sub-" + userId
        );

        // Fire the signal
        workflow.cancelSubscription();
        return "Cancellation signal sent to workflow: sub-" + userId;
    }

    @GetMapping("/status")
    public String getStatus(@RequestParam String userId) {
        // Create a stub pointing to the workflow
        SubscriptionWorkflow workflow = workflowClient.newWorkflowStub(
                SubscriptionWorkflow.class,
                "sub-" + userId
        );

        // Call the query method synchronously
        String status = workflow.getSubscriptionStatus();
        return "Current workflow status: " + status;
    }

}
