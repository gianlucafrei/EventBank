package com.example.eventbank.cards.messaging;

import com.example.eventbank.cards.dto.Message;
import com.example.eventbank.cards.dto.PhoneNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;


@Component
@Log4j2
@RequiredArgsConstructor
public class NotificationProducer {

    private final StreamBridge streamBridge;

    public void sendPhoneNotification(PhoneNotificationEvent phoneNotificationEvent) {
        Message message = new Message<>("phoneNotificationEvent", phoneNotificationEvent);
        streamBridge.send("notification-out-0", message);
        log.info("Sent phone notification to notification-out-0: {}", phoneNotificationEvent);
    }
}
