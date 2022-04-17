package com.example.eventbank.notifications.dto;


import lombok.Data;


@Data
public class EmailNotificationEvent {

    String emailAddress;
    String title;
    String message;
}
