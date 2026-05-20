package com.bank.document_service.infrastructure.generator;

import com.bank.document_service.application.port.PasswordGeneratorPort;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class SecurePasswordGeneratorAdapter implements PasswordGeneratorPort {

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=";

    private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER + SPECIAL;

    //Bezpieczniejszy random
    private final SecureRandom secureRandom = new SecureRandom();

    private static final int PASSWORD_LENGTH = 11;

    @Override
    public String generateTemporaryPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            //wybiera index
            int randomIndex = secureRandom.nextInt(PASSWORD_ALLOW_BASE.length());

            //podaje znak który znajduje się pod danym indexem który otrzymalsimy od randomIndex
            char randomChar = PASSWORD_ALLOW_BASE.charAt(randomIndex);

            //zapisuje do naszej zmiennej ten wylosowany znak
            password.append(randomChar);
        }
        return password.toString();
    }
}
