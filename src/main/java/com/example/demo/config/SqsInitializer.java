package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

import java.net.URI;

@Component
public class SqsInitializer {

    @Value("${sqs.endpoint}")
    private String sqsEndpoint;

    @Value("${sqs.queue-name}")
    private String queueName;

    @PostConstruct
    public void initializeQueue() {
        SqsClient sqsClient = SqsClient.builder()
                .endpointOverride(URI.create(sqsEndpoint))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")))
                .build();

        try {

            var queueUrlResponse = sqsClient.getQueueUrl(r -> r.queueName(queueName));
            System.out.println("✅ Fila SQS '" + queueName + "' já existe: " + queueUrlResponse.queueUrl());
        } catch (Exception e) {
            // Se não existir, cria a fila
            try {
                System.out.println("📦 Criando fila SQS '" + queueName + "'...");
                CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                        .queueName(queueName)
                        .build();
                var createQueueResponse = sqsClient.createQueue(createQueueRequest);
                System.out.println("✅ Fila SQS '" + queueName + "' criada com sucesso: " + createQueueResponse.queueUrl());
            } catch (Exception createException) {
                System.err.println("❌ Erro ao criar fila SQS: " + createException.getMessage());
            }
        } finally {
            sqsClient.close();
        }
    }
}

