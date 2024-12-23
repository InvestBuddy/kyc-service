package tech.investbuddy.kycservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "kyc_verification")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class KycVerification {

    @Id
    private UUID id;

    private UUID userId;

    @Enumerated(EnumType.STRING)
    private KycStatus status; // PENDING, VERIFIED, REJECTED

    @Column(length = 1024)
    private String url;

    public enum KycStatus {
        PENDING, APPROVED, DECLINED
    }

}


