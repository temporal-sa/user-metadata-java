package com.example.workflows;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface SimpleWorkflow {

    @WorkflowMethod(name = "SimpleJava")
    void execute(JsonNode input);
}
