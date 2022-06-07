package com.example.eventbank.cards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentRequest {

    private String sourceAccount;
    private String destinationAccount;
    private Integer amount;
    private String currency;
    private String authorizationCode;

}
