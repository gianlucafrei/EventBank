package com.example.eventbank.notifications.messaging;

import com.example.eventbank.notifications.dto.EmailNotificationEvent;
import com.example.eventbank.notifications.dto.Message;
import com.example.eventbank.notifications.dto.PhoneNotificationEvent;
import com.example.eventbank.notifications.service.EmailService;
import com.example.eventbank.notifications.service.PhoneService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@Log4j2
@RequiredArgsConstructor
public class MessagingAdapter {

    private final ObjectMapper om = new ObjectMapper();

    private final EmailService emailService;
    private final PhoneService phoneService;

    @Bean
    public Consumer<Message<?>> consumer() {
        log.info("The message is here");
        return this::consumeMessage;
    }

    public void consumeMessage(Message<?> message) {

        log.info("New Message: {}", message);

        try {

            if ("emailNotificationEvent".equals(message.getType())) {

                String json = om.writeValueAsString(message.getData());
                EmailNotificationEvent email = om.readValue(json, EmailNotificationEvent.class);
                emailService.sendEmail(email);
            }
            if ("phoneNotificationEvent".equals(message.getType())) {

                String json = om.writeValueAsString(message.getData());
                PhoneNotificationEvent phone = om.readValue(json, PhoneNotificationEvent.class);
                phoneService.sendNotification(phone);
            }

        } catch (Exception ex) {
            log.error("Error while processing incoming message", ex);
        }

    }
}
