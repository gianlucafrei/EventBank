package com.example.eventbank.notifications.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneNotificationEvent {

    String accountId;
    String paymentId;
    boolean success;
    String message;
}
