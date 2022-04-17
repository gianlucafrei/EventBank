package com.example.eventbank.cards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResultEvent {

    private String paymentId;
    private boolean success;
    private String message;
    private String debtorId;
    private String creditorId;
}
