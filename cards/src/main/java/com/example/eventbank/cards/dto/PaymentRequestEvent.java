package com.example.eventbank.cards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentRequestEvent {

    private String paymentId;
    private String sourceAccount;
    private String destinationAccount;
    private Integer amount;
    private String currency;
}
