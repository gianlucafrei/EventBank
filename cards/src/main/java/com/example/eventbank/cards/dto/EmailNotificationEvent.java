package com.example.eventbank.cards.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailNotificationEvent {

    String emailAddress;
    String title;
    String message;
}
