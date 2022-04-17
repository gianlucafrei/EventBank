package com.example.eventbank.customerregistration.adapter.in.messaging;

import com.example.eventbank.customerregistration.dto.Message;
import com.example.eventbank.customerregistration.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageProcessConsumer {

    private final static String MESSAGE_CARD_ISSUED = "MessageCardIssued";
    private final MessageService messageService;

    @Bean
    public Consumer<Message<?>> consumer() {
        return message -> System.out.println("received " + message);
    }

    @KafkaListener(topics = "notify-card-issued-topic")
    public void notifyCardIssued(Message<?> message) {
        messageService.correlateMessage(message, MESSAGE_CARD_ISSUED);
    }
}
