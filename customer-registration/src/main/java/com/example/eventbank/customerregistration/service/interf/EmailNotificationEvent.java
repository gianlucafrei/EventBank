package com.example.eventbank.customerregistration.service.interf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmailNotificationEvent {

    String emailAddress;
    String title;
    String message;
}
