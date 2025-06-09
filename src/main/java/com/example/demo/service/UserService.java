
package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.net.URI;

@Service
public class UserService {

    private final UserRepository repository;
    private final ObjectMapper objectMapper;

    @Value("${sqs.endpoint}")
    private String sqsEndpoint;

    @Value("${sqs.queue-name}")
    private String queueName;

    public UserService(UserRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public User saveAndSend(User user) {
        User saved = repository.save(user);

        SqsClient sqsClient = SqsClient.builder()
            .endpointOverride(URI.create(sqsEndpoint))
            .region(Region.US_EAST_1)
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
            .build();

        try {
            String message = objectMapper.writeValueAsString(saved);
            String queueUrl = sqsClient.getQueueUrl(r -> r.queueName(queueName)).queueUrl();
            sqsClient.sendMessage(SendMessageRequest.builder().queueUrl(queueUrl).messageBody(message).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return saved;
    }
}
