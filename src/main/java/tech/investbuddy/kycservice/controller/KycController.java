package tech.investbuddy.kycservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.investbuddy.kycservice.model.KycVerification;
import tech.investbuddy.kycservice.service.KycService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kyc")
public class KycController {

    private final KycService kycService;

    @PostMapping("/webhook/decisions")
    @ResponseStatus(HttpStatus.OK)
    public void handleWebhook(@RequestBody Map<String, Object> payload) {
        try {
            //System.out.println("payload" + payload);
            // Vérifier la validité du payload
            if (payload == null || !payload.containsKey("data")) {
                throw new IllegalArgumentException("Invalid payload: Missing 'data' key");
            }

            //System.out.println("payload" + payload);

            // Extraire les informations importantes
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            Map<String, Object> verification = (Map<String, Object>) data.get("verification");
            UUID verificationId = UUID.fromString((String) payload.get("sessionId"));
            String status = (String) verification.get("decision");

            System.out.println("Webhook received for verificationId: " + verificationId + " with status: " + status);

            // Convertir le statut en une énumération interne
            KycVerification.KycStatus verificationStatus = switch (status.toLowerCase()) {
                case "approved" -> KycVerification.KycStatus.APPROVED;
                case "declined" -> KycVerification.KycStatus.DECLINED;
                default -> KycVerification.KycStatus.PENDING;
            };

            // Mettre à jour le statut dans la base de données
            kycService.updateKycStatus(verificationId, verificationStatus);



            // (Optionnel) Traiter d'autres données utiles, comme le risque ou les informations personnelles
            /*Map<String, Object> riskScore = (Map<String, Object>) verification.get("riskScore");
            if (riskScore != null) {
                Double score = (Double) riskScore.get("score");
                System.out.println("Risk Score: " + score);
            }*/


        } catch (Exception e) {
            // Logger et retourner une erreur en cas de problème
            System.err.println("Error processing webhook: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to process webhook", e);
        }
    }

    @GetMapping("/{userId}/status")
    @ResponseStatus(HttpStatus.OK)
    public KycVerification.KycStatus getKycStatus(@PathVariable UUID userId) {
        return kycService.findKycByUserId(userId).getStatus();
    }

    @GetMapping("/{userId}/url")
    @ResponseStatus(HttpStatus.OK)
    public String getKycUrl(@PathVariable UUID userId) {
        return kycService.findKycByUserId(userId).getUrl();
    }

}
