package com.bank.card_service.infrastructure.security;

import com.bank.card_service.application.port.out.CvvGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//Symulacja bankowego modułu Hardware Security Module
@Component
public class HmacCvvGenerator implements CvvGenerator {

    private final String secretKey;
    private static final String HMAC_ALGO = "HmacSHA256";

    public HmacCvvGenerator(@Value("${bank.security.cvv-secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String generate(String cardNumber, LocalDateTime expiryDate) {
        try {

            String expiryStr = expiryDate.format(DateTimeFormatter.ofPattern("MM/yy"));
            String inputData = cardNumber + expiryStr;

            Mac mac = Mac.getInstance(HMAC_ALGO);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGO);
            mac.init(keySpec);

            byte[] hashBytes = mac.doFinal(inputData.getBytes(StandardCharsets.UTF_8));

            StringBuilder cvvBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                int digit = Math.abs(b % 10);
                cvvBuilder.append(digit);

                if (cvvBuilder.length() == 3) {
                    break;
                }
            }

            return cvvBuilder.toString();
        } catch (Exception e) {
            throw new RuntimeException("Krytyczny błąd modułu HSM podczas generowania CVV!", e);
        }
    }


}
