
package com.example.demo.listener;

import com.example.demo.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.net.URI;
import java.util.List;

@Component
public class UserListener {

    // ... [outros atributos]
    @Value("${sqs.endpoint}")
    private String sqsEndpoint;

    @Value("${sqs.queue-name}")
    private String queueName;


    private final ObjectMapper mapper = new ObjectMapper();
    private SqsClient client;


    @Autowired
    private JavaMailSender mailSender;


    @PostConstruct
    public void init() {
        client = SqsClient.builder()
                .endpointOverride(URI.create(sqsEndpoint))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")))
                .build();
    }

    @Scheduled(fixedRate = 5000)
    public void pollMessages() {
        System.out.println("🔁 Verificando mensagens na fila...");

        String queueUrl = client.getQueueUrl(r -> r.queueName(queueName)).queueUrl();
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5)
                .build();

        List<Message> messages = client.receiveMessage(request).messages();
        for (Message msg : messages) {
            try {
                User user = mapper.readValue(msg.body(), User.class);

                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(user.getEmail());
                message.setSubject("Bem-vindo!");
                message.setText("Olá " + user.getName() + ", bem-vindo ao nosso sistema!");
                mailSender.send(message);

                System.out.println("✅ Email enviado para " + user.getEmail());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}