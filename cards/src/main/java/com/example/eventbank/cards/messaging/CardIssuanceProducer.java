package com.example.eventbank.cards.messaging;

import com.example.eventbank.cards.dto.CardIssuedEvent;
import com.example.eventbank.cards.dto.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardIssuanceProducer {

    private final KafkaTemplate<String, Message<?>> kafkaTemplate;

    public void notifyCardIssued(Message<CardIssuedEvent> message) {
        log.info("Notify card issued event: {}", message.getData().toString());
        kafkaTemplate.send("notify-card-issued-topic", message);
    }
}
