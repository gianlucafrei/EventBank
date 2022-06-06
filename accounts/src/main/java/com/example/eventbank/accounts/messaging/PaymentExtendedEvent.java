package com.example.eventbank.accounts.messaging;

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

}
