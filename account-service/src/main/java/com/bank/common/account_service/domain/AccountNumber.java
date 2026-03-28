package com.bank.common.account_service.domain;

import java.math.BigInteger;
import java.util.Objects;

public class AccountNumber {

    private final String value;

    private static final String PL_COUNTRY_CODE = "2521";
    public static final String BANK_SORT_CODE = "12345678";

    public AccountNumber (String value) {
        if (value == null || !value.matches("\\d{26}")) {
            throw new IllegalArgumentException("Numer konta musi składać się z dokładnie 26 cyfr.");
        }
        if (!isValidChecksum(value)) {
            throw new IllegalArgumentException("Nieprawidłowa suma kontrolna. Ten numer konta jest fałszywy.");
        }
        this.value = value;
    }

    public static AccountNumber generateNew(String sequenceNumberFromDb) {
       if (sequenceNumberFromDb == null || !sequenceNumberFromDb.matches("\\d{16}")) {
           throw new IllegalArgumentException("Numer sekwencyjny z bazy musi mieć dokładnie 16 cyfr.");
       }

        String bban = BANK_SORT_CODE + sequenceNumberFromDb;
        String checksum = calculateChecksum(bban);
        return new AccountNumber(checksum + bban);
    }

    private static boolean isValidChecksum(String nrb) {
        String bban = nrb.substring(2);
        String checksum = nrb.substring(0, 2);
        String numberToMod = bban + PL_COUNTRY_CODE + checksum;
        BigInteger bigInt = new BigInteger(numberToMod);
        return bigInt.remainder(new BigInteger("97")).intValue() == 1;
    }

    private static String calculateChecksum(String bban) {
        String numberToMod = bban + PL_COUNTRY_CODE + "00";
        BigInteger bigInt = new BigInteger(numberToMod);
        int mod = bigInt.remainder(new BigInteger("97")).intValue();
        int checksumValue = 98 - mod;
        return String.format("%02d", checksumValue);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountNumber that = (AccountNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
