package com.bank.card_service.infrastructure.generator;

import com.bank.card_service.application.port.out.CardNumberGenerator;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class LuhnCardNumberGenerator implements CardNumberGenerator {

    private final Random random = new Random();

    private static final String BANK_BIN = "400000";

    @Override
    public String generate() {
        StringBuilder partialNumber = new StringBuilder(BANK_BIN);
        for (int i = 0; i < 9; i++) {
            partialNumber.append(random.nextInt(10));
        }

        int checkDigit = calculateLuhnCheckDigit(partialNumber.toString());

        return partialNumber.toString() + checkDigit;
    }

    private int calculateLuhnCheckDigit(String number) {
        int sum = 0;
        boolean alternate = true;

        for (int i = number.length() -1; i >= 0; i--) {
            int n = Integer.parseInt(number.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        int checkDigit = 10 - (sum % 10);
        return (checkDigit == 10) ? 0 : checkDigit;
    }
}
