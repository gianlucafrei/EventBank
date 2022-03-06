package com.example.eventbank.cards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentEvent {

    private String sourceAccount;
    private String destinationAccount;
    private Integer amount;
}
