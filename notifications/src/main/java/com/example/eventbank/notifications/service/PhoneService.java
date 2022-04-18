package com.example.eventbank.notifications.service;


import com.example.eventbank.notifications.dto.PhoneNotificationEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


@Log4j2
@Service
public class PhoneService {

    public void sendNotification(PhoneNotificationEvent phoneNotificationEvent) {
        log.info("Payment {}: {}", phoneNotificationEvent.getPaymentId(), phoneNotificationEvent.getMessage());
    }
}
