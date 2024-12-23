package tech.investbuddy.kycservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.investbuddy.kycservice.model.KycVerification;

import java.util.UUID;

public interface KycRepository extends JpaRepository<KycVerification, UUID> {
    KycVerification findByUserId(UUID userId);
}
