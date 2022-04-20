package com.example.eventbank.cards.dto;


import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class PhoneNotificationEvent {

    String accountId;
    String paymentId;
    boolean success;
    String message;
}
