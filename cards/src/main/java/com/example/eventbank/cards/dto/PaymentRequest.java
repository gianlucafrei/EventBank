package com.example.eventbank.cards.dto;

import lombok.Data;

@Data
public class PaymentRequest {

    private String sourceAccount;
    private String destinationAccount;
    private Integer amount;
    private String authorizationCode;

}
