package com.example.swingui;

import com.example.workflows.SimpleWorkflow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.micrometer.core.instrument.util.IOUtils;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.SimpleSslContextBuilder;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.WorkerFactory;
import org.json.JSONObject;

import javax.net.ssl.SSLException;
import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SwingUI {
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);
    public static final WorkerFactory factory = WorkerFactory.newInstance(client);

    public static void main(String[] args) throws FileNotFoundException, SSLException {
        String address = getEnv("TEMPORAL_ADDRESS", "127.0.0.1:7233");
        String namespace = getEnv("TEMPORAL_NAMESPACE", "default");

        SslContext sslContext = null;
//        Use the following to connect to Temporal Cloud using mTLS
        String tlsCertPath = getEnv("TEMPORAL_TLS_CERT", "");
        String tlsKeyPath = getEnv("TEMPORAL_TLS_KEY_PKCS8", "");
        String webUIPathTemplate;
        if (!tlsCertPath.isBlank() && !tlsKeyPath.isBlank()) {
            InputStream tlsCertInputStream = new FileInputStream(tlsCertPath);
            InputStream tlsKeyInputStream = new FileInputStream(tlsKeyPath);
            sslContext = SimpleSslContextBuilder.forPKCS8(tlsCertInputStream, tlsKeyInputStream).build();
            webUIPathTemplate = "https://cloud.temporal.io/namespaces/%s/workflows/%s/%s";
        } else {
            webUIPathTemplate = "http://localhost:8233/namespaces/%s/workflows/%s/%s";
        }

        WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(
                WorkflowServiceStubsOptions.newBuilder()
                        .setTarget(address)
                        .setSslContext(sslContext)
                        .build()
        );

        WorkflowClient client = WorkflowClient.newInstance(service,
                WorkflowClientOptions.newBuilder()
                        .setNamespace(namespace)
                        .build()
        );

        factory.start();
        // Create a new window (frame)
        JFrame frame = new JFrame("User Metadata Workflow Starter");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null); // Absolute positioning

        // Create a label
        JLabel summaryLabel = new JLabel("Static Summary");
        summaryLabel.setBounds(10, 20, 780, 25);
        frame.add(summaryLabel);

        // Create a text field
        JTextField summaryText = new JTextField();
        summaryText.setBounds(10, 50, 780, 25);
        frame.add(summaryText);

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setBounds(10, 75, 780, 25);
        frame.add(nameLabel);
        JTextField nameText = new JTextField();
        nameText.setBounds(10, 100, 780, 25);
        frame.add(nameText);

        // Add checkbox for static details
        JCheckBox staticDetailsCheckbox = new JCheckBox("Set Static Details?");
        staticDetailsCheckbox.setBounds(10, 125, 780, 25);
        frame.add(staticDetailsCheckbox);
        // Add checkbox for setting current details
        JCheckBox currentDetailsCheckbox = new JCheckBox("Set Current Details?");
        currentDetailsCheckbox.setBounds(10, 150, 780, 25);
        frame.add(currentDetailsCheckbox);
        // Add checkbox for timer decoration
        JCheckBox decorateTimersCheckbox = new JCheckBox("Decorate Timers?");
        decorateTimersCheckbox.setBounds(10, 175, 780, 25);
        frame.add(decorateTimersCheckbox);
        JCheckBox decorateActivitiesCheckbox = new JCheckBox("Decorate Activities?");
        decorateActivitiesCheckbox.setBounds(10, 200, 780, 25);
        frame.add(decorateActivitiesCheckbox);
        // Add radio buttons for method to use
        JLabel methodLabel = new JLabel("Which method should be called?");
        methodLabel.setBounds(10, 225, 780, 25);
        frame.add(methodLabel);
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton sayHello = new JRadioButton("sayHello");
        sayHello.setBounds(10, 250, 780, 25);
        sayHello.setSelected(true);
        frame.add(sayHello);
        buttonGroup.add(sayHello);
        JRadioButton sayGoodbye = new JRadioButton("sayGoodbye");
        sayGoodbye.setBounds(10, 275, 780, 25);
        frame.add(sayGoodbye);
        buttonGroup.add(sayGoodbye);
        // Create a button
        JButton button = new JButton("Start workflow");
        button.setBounds(10, 300, 120, 30);
        frame.add(button);
        String workflowId = UUID.randomUUID().toString();
        // Add action to the button
        button.addActionListener(e -> {
            String staticSummaryDetails = summaryText.getText();
            WorkflowOptions.Builder optionsBuilder = WorkflowOptions.newBuilder()
                    .setWorkflowId(workflowId)
                    .setTaskQueue("simple-task-queue")
                    .setStaticSummary(staticSummaryDetails);

            if (staticDetailsCheckbox.isSelected()) {
                try {
                    InputStream detailsStream = new FileInputStream("workflow-details.md");
                    String details = IOUtils.toString(detailsStream, StandardCharsets.UTF_8);
                    optionsBuilder.setStaticDetails(details);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, ex.toString());
                }
            }
            SimpleWorkflow workflow = client.newWorkflowStub(
                    SimpleWorkflow.class,
                    optionsBuilder.build());

            try {
                JSONObject inputObj = new JSONObject();
                if (decorateTimersCheckbox.isSelected()) {
                    inputObj.put("decorateTimers", true);
                }
                if (currentDetailsCheckbox.isSelected()) {
                    inputObj.put("setCurrentDetails", true);
                }
                if (decorateActivitiesCheckbox.isSelected()) {
                    inputObj.put("decorateActivities", true);
                }
                if (sayHello.isSelected()) {
                    inputObj.put("toolName", "sayHello");
                }
                if (sayGoodbye.isSelected()) {
                    inputObj.put("toolName", "sayGoodbye");
                }
                inputObj.put("name", nameText.getText());
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode inputNode = objectMapper.readTree(inputObj.toString());

                WorkflowExecution execution = WorkflowClient.start(workflow::execute, inputNode);
                String runId = execution.getRunId();
                String temporalUrl = String.format(webUIPathTemplate, namespace, workflowId, runId);
                try {
                    URI uri = new URI(temporalUrl);
                    Desktop.getDesktop().browse(uri);
                } catch (URISyntaxException | IOException ex) {
                    JOptionPane.showMessageDialog(frame, ex.toString());
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, ex.toString());
            }
            JOptionPane.showMessageDialog(frame, "Workflow started");
        });

        // Make the frame visible
        frame.setVisible(true);
    }

    private static String getEnv(String key, String fallback) {
        return System.getenv().getOrDefault(key, fallback);
    }
}
