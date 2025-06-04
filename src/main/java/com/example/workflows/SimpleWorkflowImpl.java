package com.example.workflows;

import com.example.activities.Tools;
import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Promise;
import io.temporal.workflow.TimerOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class SimpleWorkflowImpl implements SimpleWorkflow {

    private static final Logger log = Workflow.getLogger(SimpleWorkflowImpl.class);

    @Override
    public void execute(JsonNode input) {
        String toolName  =  input.get("toolName").asText();
        Boolean setCurrentDetails;
        JsonNode setCurrentDetailsNode = input.get("setCurrentDetails");
        if (setCurrentDetailsNode == null) {
            setCurrentDetails = false;
        } else {
            setCurrentDetails = input.get("setCurrentDetails").asBoolean();
        }
        RetryOptions.Builder retryOpts = RetryOptions.newBuilder()
                .setInitialInterval(Duration.ofSeconds(1))
                .setBackoffCoefficient(2)
                .setMaximumInterval(Duration.ofSeconds(30));
        ActivityOptions.Builder actOptsBuilder = ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofSeconds(5))
                .setRetryOptions(retryOpts.build());
        if (input.get("decorateActivities") != null) {
            if (input.get("decorateActivities").asBoolean()) {
                actOptsBuilder.setSummary("toolName=" + toolName);
            }
        }
        Tools tools = Workflow.newActivityStub(Tools.class, actOptsBuilder.build());
        log.info("Simple workflow started, input = {}", input);
        TimerOptions.Builder timerOptions1 = TimerOptions.newBuilder();
        JsonNode decorateTimersNode = input.get("decorateTimers");
        if (decorateTimersNode != null) {
            if (input.get("decorateTimers").asBoolean()) {
                timerOptions1.setSummary("Timer #1 \uD83D\uDE38");
            }
        }

        Promise<Void> t1 = Workflow.newTimer(Duration.ofSeconds(1), timerOptions1.build());
        if (setCurrentDetails) {
            Workflow.setCurrentDetails("# I can put markdown here too\nHere is another [link](https://temporal.io/blog)");
        }
        try {
            t1.get();
            tools.run(input);
        } catch (Exception e) {
            log.error(e.toString());
            // More robust error handling recommended for production applications
        }
        TimerOptions.Builder timerOptions2 = TimerOptions.newBuilder();
        if (decorateTimersNode != null) {
            if (input.get("decorateTimers").asBoolean()) {
                timerOptions2.setSummary("Timer #2 \uD83D\uDE3C");
            }
        }

        Promise<Void> t2 = Workflow.newTimer(Duration.ofSeconds(10), timerOptions2.build());
        t2.get();
        if (setCurrentDetails) {
            Workflow.setCurrentDetails("Timer # 2 fired \uD83D\uDE00");
        }
    }
}
