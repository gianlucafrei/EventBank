package com.example.eventbank.currencies.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentExtendedEvent {

    private String paymentId;
    private String sourceAccount;
    private String destinationAccount;
    private Integer amount;
    private Integer originalAmount;
    private String currency;
    private String originalCurrency;
    private Double exchangeRate;

    public PaymentExtendedEvent(PaymentRequestEvent request) {

    }

    public static Message<PaymentExtendedEvent> paymentExtendedEventMessage(PaymentRequestEvent request) {
        return new Message<>("paymentExtended", PaymentExtendedEvent.builder()
                .paymentId(request.getPaymentId())
                .sourceAccount(request.getSourceAccount())
                .destinationAccount(request.getDestinationAccount())
                .amount(request.getAmount())
                .originalAmount(request.getAmount())
                .currency(request.getCurrency())
                .originalCurrency(request.getCurrency())
                .exchangeRate(1.0)
                .build());
    }
}
