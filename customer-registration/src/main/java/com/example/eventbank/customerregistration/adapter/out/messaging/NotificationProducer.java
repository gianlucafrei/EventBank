package com.example.eventbank.customerregistration.adapter.out.messaging;


import com.example.eventbank.customerregistration.dto.Message;
import com.example.eventbank.customerregistration.service.interf.EmailNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;


@Component
@Log4j2
@RequiredArgsConstructor
public class NotificationProducer {

    private final StreamBridge streamBridge;

    public void sendEmailNotification(EmailNotificationEvent emailNotificationEvent) {
        Message<EmailNotificationEvent> message = new Message<>("emailNotificationEvent", emailNotificationEvent);
        streamBridge.send("notification-out-0", message);
        log.info("Sent email notification to notification-out-0: {}", emailNotificationEvent);
    }
}
