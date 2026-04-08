package com.bank.payment_service.infrastructure.in.web;

import com.bank.payment_service.application.port.in.CreatePaymentCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Obiekt żądania inicjacji nowej płatności")
public record PaymentRequestDto(

        @Schema(description = "ID konta zlecającego", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull(message = "ID konta źródłowego jest wymagane.")
        UUID sourceAccountId,

        @Schema(description = "ID konta odbiorcy", example = "987e6543-e21b-34d3-b123-526614174999")
        @NotNull(message = "ID konta docelowego jest wymagane.")
        UUID destinationAccountId,

        @Schema(description = "Kwota płatności", example = "150.50")
        @NotNull(message = "Kwota przelewu jest wymagana.")
        @Positive(message = "Kwota przelewu musi być większa od zera.")
        BigDecimal amount,

        @Schema(description = "Waluta (np. PLN, EUR, USD)", example = "PLN")
        @NotBlank(message = "Waluta jest wymagana.")
        @Pattern(regexp = "^(PLN|EUR|USD)$", message = "Obsługiwane waluty to wyłącznie PLN, EUR lub USD.")
        String currency,

        @Schema(description = "Typ płatności: TRANSFER lub CARD_PAYMENT", example = "TRANSFER")
        @NotBlank(message = "Typ płatności jest wymagany.")
        @Pattern(regexp = "^(TRANSFER|CARD_PAYMENT)$", message = "Niedozwolony typ płatności. Wybierz TRANSFER lub CARD_PAYMENT.")
        String type
) {
    public CreatePaymentCommand toCommand() {
        return new CreatePaymentCommand(
                this.sourceAccountId,
                this.destinationAccountId,
                this.amount,
                this.currency,
                this.type
        );
    }
}