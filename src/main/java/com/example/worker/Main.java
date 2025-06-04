package com.example.worker;
import com.example.activities.ToolsImpl;
import com.example.workflows.SimpleWorkflowImpl;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.SimpleSslContextBuilder;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class Main {
    public static final String TASK_QUEUE = "simple-task-queue";

    public static void main(String[] args) throws IOException {

        String address = getEnv("TEMPORAL_ADDRESS", "127.0.0.1:7233");
        String namespace = getEnv("TEMPORAL_NAMESPACE", "default");

        SslContext sslContext = null;
//        Use the following to connect to Temporal Cloud using mTLS
        String tlsCertPath = getEnv("TEMPORAL_TLS_CERT", "");
        String tlsKeyPath = getEnv("TEMPORAL_TLS_KEY_PKCS8", "");
        if (!tlsCertPath.isBlank() && !tlsKeyPath.isBlank()) {
            InputStream tlsCertInputStream = new FileInputStream(tlsCertPath);
            InputStream tlsKeyInputStream = new FileInputStream(tlsKeyPath);
            sslContext = SimpleSslContextBuilder.forPKCS8(tlsCertInputStream, tlsKeyInputStream).build();
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

        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker(TASK_QUEUE);
        worker.registerWorkflowImplementationTypes(SimpleWorkflowImpl.class);
        worker.registerActivitiesImplementations(new ToolsImpl());
        factory.start();
    }

    private static String getEnv(String key, String fallback) {
        return System.getenv().getOrDefault(key, fallback);
    }

}
