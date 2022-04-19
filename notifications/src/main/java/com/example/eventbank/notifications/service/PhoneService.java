package com.example.eventbank.notifications.service;


import com.example.eventbank.notifications.dto.PhoneNotificationEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


@Log4j2
@Service
public class PhoneService {

    private static int counter_failure = 0;
    private static int counter = 0;

    public void sendNotification(PhoneNotificationEvent phoneNotificationEvent) {
        if (phoneNotificationEvent.getMessage().contains("Failed")) {
            counter_failure++;
        }
        log.info("[{}/{}] Payment {}: {}", counter_failure, ++counter,
                phoneNotificationEvent.getPaymentId(), phoneNotificationEvent.getMessage());
    }
}
