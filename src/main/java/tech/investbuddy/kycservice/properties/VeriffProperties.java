package tech.investbuddy.kycservice.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class VeriffProperties {

    @Value("${veriff.api.key}")
    private String apiKey;

    @Value("${veriff.api.base.url}")
    private String baseUrl;

}