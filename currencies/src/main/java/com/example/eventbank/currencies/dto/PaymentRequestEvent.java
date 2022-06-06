package com.example.eventbank.currencies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestEvent {

    private String paymentId;
    private String sourceAccount;
    private String destinationAccount;
    private Integer amount;
    private String currency;
}
