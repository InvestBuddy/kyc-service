package tech.investbuddy.kycservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.investbuddy.kycservice.properties.VeriffProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper objectMapper; // Jackson ObjectMapper pour parser JSON
    private final RestTemplate restTemplate = new RestTemplate();

    private final VeriffProperties veriffProperties;

    private final KycService kycService;

    @KafkaListener(topics = "kyc-verification", groupId = "kyc-group")
    public void handleUserKycVerification(String message) {
        System.out.println("Message reçu : " + message);

        try {
            // Parsez le message JSON
            JsonNode jsonNode = objectMapper.readTree(message);

            UUID userId = UUID.fromString(jsonNode.get("userId").asText());
            String firstName = jsonNode.get("firstName").asText();
            String lastName = jsonNode.get("lastName").asText();

            Map<String, Object> session = createVeriffSession(userId, firstName, lastName);

            if (session != null) {
                kycService.createKyc(userId, UUID.fromString(session.get("id").toString()), session.get("url").toString());
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du traitement du message Kafka : " + e.getMessage());
        }
    }

    public Map<String, Object> createVeriffSession(UUID userId, String firstName, String lastName) {
        String endpoint = veriffProperties.getBaseUrl() + "/v1/sessions";

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-AUTH-CLIENT", veriffProperties.getApiKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.setBasicAuth("api", veriffProperties.getApiKey());

        // Request Body
        Map<String, Object> body = new HashMap<>();
        body.put("verification", Map.of(
                "callback", "https://localhost:3000", // URL de redirection après vérification
                "person", Map.of(
                        "firstName", firstName,
                        "lastName", lastName
                ),
                "endUserId", userId.toString()
        ));

        System.out.println("body to sent:" + body);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // API Call
        ResponseEntity<Map> response = restTemplate.exchange(
                endpoint,
                HttpMethod.POST,
                request,
                Map.class
        );

        // Handle response
        if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
            Map<String, Object> verificationData = (Map<String, Object>) response.getBody().get("verification");

            System.out.println("verificationData : " + verificationData);

            if (verificationData != null) {
                return Map.of(
                        "id", verificationData.get("id").toString(),
                        "url", verificationData.get("url").toString()
                );
            }
        }

        throw new RuntimeException("Failed to create Veriff session: " + response.getStatusCode());
    }
}
