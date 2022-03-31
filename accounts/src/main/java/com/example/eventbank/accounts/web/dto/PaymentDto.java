package com.example.eventbank.accounts.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentDto {

    private String creditorId;
    private String reservationId;
    private Integer amount;

}
