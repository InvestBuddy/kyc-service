package tech.investbuddy.kycservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.investbuddy.kycservice.model.KycVerification;
import tech.investbuddy.kycservice.repository.KycRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KycService {
    private final KycRepository kycRepository;

    public void createKyc(UUID userId, UUID kycId, String url) {
        KycVerification verification = new KycVerification();
        verification.setUserId(userId);
        verification.setId(kycId);
        verification.setStatus(KycVerification.KycStatus.PENDING);
        verification.setUrl(url);

        kycRepository.save(verification);
    }

    public void updateKycStatus(UUID kycId, KycVerification.KycStatus newStatus) {
        KycVerification verification = kycRepository.findById(kycId)
                .orElseThrow(() -> new IllegalArgumentException("KYC avec l'ID " + kycId + " n'a pas été trouvé."));
        verification.setStatus(newStatus);
        kycRepository.save(verification);
    }


    public KycVerification findKycByUserId(UUID userId) {
        return kycRepository.findByUserId(userId);
    }
}
