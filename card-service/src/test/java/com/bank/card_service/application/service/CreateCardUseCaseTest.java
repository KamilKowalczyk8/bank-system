package com.bank.card_service.application.service;

import com.bank.card_service.application.port.out.*;
import com.bank.card_service.domain.*;
import com.bank.card_service.infrastructure.dto.event.CardCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCardUseCaseTest {

    @Mock private CardRepository cardRepository;
    @Mock private CardNumberGenerator cardNumberGenerator;
    @Mock private PinHasher pinHasher;
    @Mock private CardFactory cardFactory;
    @Mock private CvvGenerator cvvGenerator;
    @Mock private CardEventPublisher cardEventPublisher;
    @Mock private CustomerProvider customerProvider;

    @InjectMocks
    private CreateCardUseCase createCardUseCase;

    @Test
    void shouldExecuteFullCardCreationProcess() {
        UUID accountId = UUID.randomUUID();
        String rawPin = "1234";

        String rawCardNumber = "1234567890123456";
        CardNumber generatedNumber = new CardNumber(rawCardNumber);
        String hashedPin = "hashed_1234";
        String generatedCvv = "999";
        String customerEmail = "test@bank.pl";

        LocalDateTime expectedExpiryDate = LocalDateTime.now().plusYears(3);

        Card fakeCard = new Card(UUID.randomUUID(), generatedNumber, accountId, expectedExpiryDate, hashedPin, new BigDecimal("5000.00"), generatedCvv);

        when(cardNumberGenerator.generate()).thenReturn(rawCardNumber);

        when(cardRepository.existsByCardNumber(any(CardNumber.class))).thenReturn(false);

        when(pinHasher.hash(rawPin)).thenReturn(hashedPin);
        when(cardFactory.calculateExpiryDate()).thenReturn(expectedExpiryDate);
        when(cvvGenerator.generate(eq(rawCardNumber), eq(expectedExpiryDate))).thenReturn(generatedCvv);

        when(cardFactory.createNew(eq(accountId), any(CardNumber.class), eq(hashedPin), eq(expectedExpiryDate), eq(generatedCvv)))
                .thenReturn(fakeCard);

        when(cardRepository.save(fakeCard)).thenReturn(fakeCard);
        when(customerProvider.getCustomerEmail(accountId)).thenReturn(customerEmail);

        Card result = createCardUseCase.execute(accountId, rawPin);

        assertNotNull(result, "Wynik nie może być nullem");
        assertEquals(fakeCard.getId(), result.getId(),"Powinna zostać zwrócona wygenerowana karta");

        verify(cardRepository).existsByCardNumber(any(CardNumber.class));
        verify(cardRepository).save(fakeCard);
        verify(cardEventPublisher).publishCardCreated(any(CardCreatedEvent.class));
    }
}
