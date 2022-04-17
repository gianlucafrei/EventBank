package com.example.eventbank.notifications.service;


import com.example.eventbank.notifications.dto.EmailNotificationEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


@Log4j2
@Service
public class EmailService {

    public void sendEmail(EmailNotificationEvent emailNotificationEvent) {
        log.info("Sending Email to {}", emailNotificationEvent.getEmailAddress());
        log.info(" - Email title {}", emailNotificationEvent.getTitle());
        log.info(" - Email message {}", emailNotificationEvent.getMessage());
    }
}
