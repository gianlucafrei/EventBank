package com.example.eventbank.cards.messaging;

import com.example.eventbank.cards.dto.Message;
import com.example.eventbank.cards.dto.PaymentResultEvent;
import com.example.eventbank.cards.service.CardPaymentService;
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
    private CardPaymentService cardPaymentService;


    @Bean
    public Consumer<Message> paymentReply() {
        return this::consumeMessage;
    }


    public void consumeMessage(Message message) {

        log.info("New Message: {}", message);

        try {

            if ("paymentResultEvent".equals(message.getType())) {

                String json = om.writeValueAsString(message.getData());
                PaymentResultEvent event = om.readValue(json, PaymentResultEvent.class);
                cardPaymentService.handlePaymentResultEvent(event);

            }
        } catch (Exception ex) {
            log.error("Error while processing incoming message", ex);
        }

    }
}
