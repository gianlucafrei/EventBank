package com.example.eventbank.accounts.messaging;

import com.example.eventbank.accounts.dto.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@Log4j2
public class MessagingAdapter {

    private final ObjectMapper om = new ObjectMapper();
    @Autowired
    private PaymentsAdapter paymentsAdapter;


    @Bean
    public Consumer<Message> paymentRequest() {

        return this::consumeMessage;
    }

    public void consumeMessage(Message message) {

        log.info("New Message: {}", message);

        try {

            if ("paymentEvent".equals(message.getType())) {

                String json = om.writeValueAsString(message.getData());
                PaymentRequestEvent event = om.readValue(json, PaymentRequestEvent.class);
                paymentsAdapter.consumePaymentMessage(event);
            }
        } catch (Exception ex) {
            log.error("Error while processing incoming message", ex);
        }

    }
}
