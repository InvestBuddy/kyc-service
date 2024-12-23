package tech.investbuddy.kycservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
        // Extraire l'ID de session et le statut
        UUID verificationId = (UUID) ((Map<String, Object>) payload.get("resource")).get("id");
        String status = (String) ((Map<String, Object>) payload.get("resource")).get("status");

        // Convertir le statut reçu en un statut interne (ENUM VerificationStatus)
        KycVerification.KycStatus verificationStatus = switch (status) {
            case "approved" -> KycVerification.KycStatus.APPROVED;
            case "declined" -> KycVerification.KycStatus.DECLINED;
            default -> KycVerification.KycStatus.PENDING;
        };

        // Mettre à jour le statut de l'utilisateur dans la base
        //userService.updateVerificationStatus(verificationId, verificationStatus);
        kycService.updateKycStatus(verificationId, verificationStatus);
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
