package com.example.eventbank.accounts.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResultEvent {

    private String paymentId;
    private boolean success;
    private String message;
}
